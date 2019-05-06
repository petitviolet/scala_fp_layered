package net.petitviolet.example

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{ Route, RouteConcatenation }
import cats.Monad
import net.petitviolet.edatetime.EDateTime
import net.petitviolet.example.applications.dtos.UserDto
import net.petitviolet.example.commons.DateTime
import net.petitviolet.example.domains.impl.{ AsyncIO, UserRepositoryImpl }
import net.petitviolet.example.domains.users.UserRepository
import spray.json.{ DefaultJsonProtocol, JsString, JsValue, RootJsonFormat }
import wvlet.airframe.Design

import scala.concurrent.ExecutionContext

package object controllers extends SprayJsonSupport with DefaultJsonProtocol {
  def route: Route = RouteConcatenation.concat(
    GetAllUserController,
    UpdateUserController,
    HealthController
  )

  implicit def futureInstance(implicit ec: ExecutionContext) =
    cats.instances.future.catsStdInstancesForFuture
  implicit val userRepository: UserRepository[AsyncIO] = UserRepositoryImpl

  implicit lazy val datetimeJ = new RootJsonFormat[DateTime] {
    override def read(json: JsValue): DateTime =
      json match {
        case JsString(value) =>
          EDateTime.`from_yyyy-MM-dd HH:mm:ss`(value)
        case _ =>
          throw new RuntimeException(s"invalid datetime format. value = $json")
      }

    override def write(obj: DateTime): JsValue = JsString(obj.`yyyy-MM-dd HH:mm:ss`)
  }

  implicit lazy val userDtoJ = jsonFormat6(UserDto.apply)

  def newDesign[F[_]: Monad: UserRepository]() =
    wvlet.airframe.newDesign
      .bind[UserRepository[F]]
      .toInstance(implicitly)
      .bind[Monad[F]]
      .toInstance(implicitly)

  def design(implicit M: Monad[AsyncIO], ec: ExecutionContext): Design = newDesign[AsyncIO]()
}
