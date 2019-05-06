package net.petitviolet.example.infra.daos

import java.time.{ ZoneId, ZonedDateTime }

import com.typesafe.config.{ Config, ConfigFactory }
import net.petitviolet.example.commons.LoggerProvider
import scalikejdbc._
import scalikejdbc.config._
import com.zaxxer.hikari.HikariDataSource
import skinny.orm._

import scala.concurrent.{ ExecutionContext, Future }
import scala.io.Source
import scala.util.Try

sealed abstract class Database(val dbName: Symbol)
    extends DBs
    with TypesafeConfigReader
    with TypesafeConfig {

  override val config: Config = ConfigFactory.load()

  /**
   * DBに疎通できるかどうかの確認
   */
  def healthCheck(): Try[Unit] = Try {
    withRead { implicit s =>
      SQL("SELECT 1")
        .map { rs =>
          rs.get[Int](1)
        }
        .single()
        .apply()
      ()
    }
  }

  private def connection: DBConnection = {
    scalikejdbc.NamedDB(dbName).autoClose(true)
  }

  def withInTx[A](execution: DBSession => A): A = {
    connection withinTx execution
  }

  def localTx[A](execution: DBSession => A): A = {
    connection localTx execution
  }

  def localTxAsync[A](execution: DBSession => Future[A])(
      implicit ec: ExecutionContext): Future[A] = {
    connection futureLocalTx execution
  }

  def withRead[A](execution: DBSession => A): A = {
    connection readOnly execution
  }

  def withReadAsync[A](execution: DBSession => Future[A])(
      implicit ec: ExecutionContext): Future[A] = {
    LoanPattern.futureUsing(connection.readOnlySession())(execution)(ec)
  }

  private[infra] lazy val source: HikariDataSource = {
    val _conf = config.getConfig(s"db.${dbName.name}")

    val ds = new HikariDataSource()

    ds.setJdbcUrl(_conf.getString("url"))
    ds.setUsername(_conf.getString("user"))
    ds.setPassword(_conf.getString("password"))
    ds.setPoolName(dbName.name)
    // ds.setSchema(dbName.name)
    ds.setMaximumPoolSize(3)

    val driver = _conf.getString("driver")
    Class.forName(driver)
    ds.setDriverClassName(driver)

    ds
  }

  private[infra] final def setup(): Unit = {
    ConnectionPool.add(dbName, new DataSourceConnectionPool(source))
  }

  private[infra] final def close(): Unit = {
    source.close()
  }
}

object Database extends LoggerProvider {
  private[daos] def generateId: String = java.util.UUID.randomUUID().toString

  def setup(): Unit = {
    scalikejdbc.config.DBs.loadGlobalSettings()

    dbs foreach { _.setup() }

    initialize()
  }

  private def initialize() = {
    import scalikejdbc._
    val upSQL =
      Source.fromFile("modules/infra/src/main/resources/sql/up.sql").mkString
    logger.info("=====")
    logger.info(upSQL)
    logger.info("=====")

    SampleDB localTx { implicit s =>
      SQL(upSQL).execute().apply()

      val dateTime = now()
      (
        ("11111111-1111-1111-1111-111111111111",
         "alice@example.com",
         "alice",
         "activated",
         "public") ::
          ("22222222-2222-2222-2222-222222222222", "bob@example.com", "bob", "activated", "public") ::
          ("33333333-3333-3333-3333-333333333333",
         "charlie@example.com",
         "charlie",
         "activated",
         "public") ::
          Nil
      ) foreach {
        case (id, email, name, status, visibility) =>
          logger.info(s"id: $id, name: $name")
          User.insert(User(id, email, name, status, visibility, dateTime, dateTime))
      }
    }

    logger.info(s"finished initializing database.")
  }

  def shutDown(): Unit = {
    dbs foreach { _.close() }
  }

  private val dbs: Seq[Database] = SampleDB :: Nil

  case object SampleDB extends Database('sample)

  private val ZONE_ID = ZoneId.of("Asia/Tokyo")
  def now() = ZonedDateTime.now(ZONE_ID)
}

sealed trait ORMapper[T] extends SkinnyMapperBase[T] {
  private[daos] def db: Database = Database.SampleDB

  final override lazy val schemaName = Some(db.dbName.name)

  protected def _tableName: String

  final override lazy val tableName: String = _tableName

  final override lazy val connectionPoolName: Any = db.dbName

  final override lazy val column = super.column

  // 不用意にWriteSessionにならないように
  override def autoSession: DBSession =
    throw new RuntimeException("you cannot use AutoSession!")
}

trait ORMapperWithNoId[T] extends SkinnyNoIdCRUDMapper[T] with ORMapper[T] {}

trait ORMapperWithStringId[T] extends SkinnyCRUDMapperWithId[String, T] with ORMapper[T] {
  override def useExternalIdGenerator: Boolean = true

  override def generateId: String = Database.generateId

  override def idToRawValue(id: String): Any = id

  override def rawValueToId(value: Any): String = value.toString
}

trait ORMapperRel[T] extends SkinnyJoinTable[T] with ORMapper[T]
