package net.petitviolet.example.controllers

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import cats.data.NonEmptyList
import net.petitviolet.example.commons.LoggerProvider
import spray.json._

import scala.concurrent.{ ExecutionContext, Future }

abstract class Controller
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with Directives
    with Route
    with LoggerProvider {
  protected def parallelism: Int

  protected implicit lazy val executionContext: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(parallelism))

  def route: Route

  final override def apply(v1: RequestContext): Future[RouteResult] = route(v1)

  private implicit val errorMessageResponseF: RootJsonFormat[ErrorMessageResponse] = jsonFormat1(
    ErrorMessageResponse.apply)
  private implicit val messageResponseF: RootJsonFormat[MessageResponse] =
    jsonFormat1(MessageResponse.apply)

  protected implicit lazy val dateTimeFormat =
    new RootJsonFormat[ZonedDateTime] {
      val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      override def read(json: JsValue): ZonedDateTime = {
        json match {
          case JsString(str) => ZonedDateTime.parse(str, pattern)
          case _ =>
            throw new RuntimeException(s"cannot parse $json to ZonedDateTime")
        }
      }

      override def write(obj: ZonedDateTime): JsValue =
        JsString(obj.format(pattern))
    }

  def ok[A](contents: A)(implicit format: JsonWriter[A]): StandardRoute =
    complete(contents.toJson)

  def ok(message: String): StandardRoute =
    complete(MessageResponse(message).toJson)

  def badRequest(msg: String): StandardRoute =
    complete((StatusCodes.BadRequest, ErrorMessageResponse(msg).toJson))

  def serverError(msg: String): StandardRoute =
    complete((StatusCodes.InternalServerError, ErrorMessageResponse(msg).toJson))
}

private case class ErrorMessageResponse(error: String)

private case class MessageResponse(message: String)

object Controller extends DefaultJsonProtocol {}
