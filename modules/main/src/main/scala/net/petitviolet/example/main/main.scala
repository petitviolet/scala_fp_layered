package net.petitviolet.example.main

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import net.petitviolet.example.commons.LoggerProvider
import net.petitviolet.example.controllers
import net.petitviolet.example.controllers.{
  GetAllUserController,
  HealthController,
  UpdateUserController
}
import net.petitviolet.example.infra.daos.Database

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

object main extends Application {
  override protected val applicationName: String = "app"

  override protected lazy val routes: Route = controllers.route
}

trait Application extends Directives with LoggerProvider {
  protected def applicationName: String
  protected def routes: Route

  private implicit lazy val actorSystem = ActorSystem(applicationName)
  private implicit lazy val materializer: ActorMaterializer =
    ActorMaterializer()(actorSystem)
  private val TERMINATE_DURATION = 10.seconds
  private val config = ConfigFactory.load()

  private lazy val port: Int = config.getInt(s"$applicationName.port")
  private lazy val host: String = config.getString(s"$applicationName.host")

  private val http: AtomicReference[HttpExt] = new AtomicReference()
  private val bind: AtomicReference[Future[ServerBinding]] =
    new AtomicReference()

  protected def healthPath: String = "/health"

  protected def initialize(): Unit = {
    Database.setup()
  }

  /**
   * HTTPサーバーの起動
   */
  def startServer(): Unit = synchronized {
    logger.info(s"application starting...")
    initialize()

    http.set(Http.apply(actorSystem))
    bind.set(http.get().bindAndHandle(routes, host, port))

    // 自分自身に一度リクエストを送信する
    checkTestRequest() match {
      case Success(()) =>
        // 成功すればそのまま起動
        logger.info(s"application-start succeeded - [$host:$port]")
      case Failure(t) =>
        // 失敗すれば停止
        logger.info(s"application-start failed - [$host:$port]", t)
        stopServer()
    }

  }

  protected def cleanUp(): Unit = {
    Database.shutDown()
  }

  /**
   * サーバーを停止する
   */
  def stopServer(): Unit = synchronized {
    implicit val ec = ExecutionContext.global

    logger.info("application shutting down...")
    val f = http
      .get()
      .shutdownAllConnectionPools()
      .flatMap { _ =>
        bind
          .get()
          .flatMap { _.terminate(hardDeadline = 10.seconds) }
          .flatMap { _ =>
            cleanUp()
            actorSystem.registerOnTermination {
              logger.info(s"actor system terminated")
            }
            actorSystem.terminate()
          }
      }

    f.onComplete {
      case Success(x) =>
        logger.info(s"application shutting down complete: $x")
      case Failure(t) =>
        logger.info(s"application shutting down failed: $t", t)
    }

    Await.ready(f, TERMINATE_DURATION)
    Thread.sleep(1000L)
    logger.info(s"stopServer completed.")
  }

  private def checkTestRequest(): Try[Unit] = {
    implicit val ec =
      ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
    Database.SampleDB.healthCheck().map { _ =>
      val req: HttpRequest =
        HttpRequest(HttpMethods.GET, uri = Uri(s"http://$host:$port$healthPath"))
      val res = http.get().singleRequest(req)

      logger.info(s"check-self-request: ${Await.result(res, 10.seconds)}")
    }
  }

  def main(args: Array[String]): Unit = {
    startServer()
    concurrent.blocking {
      val _ = scala.io.StdIn
        .readLine("=================\nPress RETURN to stop server...\n==============\n")
      stopServer()
    }
    logger.info(s"ﾉｼ")
  }
}
