package net.petitviolet.example.controllers

import akka.http.scaladsl.server.Route

object HealthController extends Controller {
  override protected def parallelism: Int = 1

  override def route: Route = path("health") {
    ok("[health-check]ok")
  }
}
