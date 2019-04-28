name := "scala_fp_layered"

val scala = "2.12.8"

def commonSettings(moduleName: String) = List(
  name := moduleName,
  organization := "net.petitviolet",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := scala,
  scalacOptions ++= List(
    "-language:higherKinds",
    "-language:implicitConversions"
  ),
  scalafmtOnCompile := true,
  scalafmtSbtCheck := true,
  connectInput := true,
  trapExit := false,
  libraryDependencies ++= List(
    "net.petitviolet" %% "operator" % "0.5.0",
    "net.petitviolet" %% "edatetime" % "0.3.0",
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.typelevel" %% "cats-core" % "1.5.0",
    "com.beachape" %% "enumeratum" % "1.5.13",
    "org.wvlet.airframe" %% "airframe" % "0.79"
  )
)

lazy val webAppDependencies = {
  val akkaVersion = "2.5.19"
  val akkaHttpVersion = "10.1.7"
  Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  )
}

lazy val databaseDependencies = {
  val SCALIKE_JDBC = "3.3.2"
  val SKINNY = "3.0.1"
  Seq(
    "com.h2database" % "h2" % "1.4.197",
    "com.zaxxer" % "HikariCP" % "3.3.0",
    "org.scalikejdbc" %% "scalikejdbc" % SCALIKE_JDBC,
    "org.scalikejdbc" %% "scalikejdbc-config" % SCALIKE_JDBC,
    "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % SCALIKE_JDBC,
    "org.skinny-framework" %% "skinny-orm" % SKINNY,
    "org.skinny-framework" %% "skinny-task" % SKINNY,
  )
}

lazy val scala_fp_layered = (project in file("."))
  .settings(commonSettings("scala_fp_layered"))
  .aggregate(applications, controllers, domains, infra)

lazy val main = (project in file("modules/main"))
  .settings(commonSettings("main"))
  .dependsOn(controllers)

lazy val controllers = (project in file("modules/controllers"))
  .settings(commonSettings("controllers"))
  .settings(libraryDependencies ++= webAppDependencies)
  .dependsOn(applications, infra)

lazy val applications = (project in file("modules/applications"))
  .settings(commonSettings("applications"))
  .dependsOn(domains)

lazy val domains = (project in file("modules/domains"))
  .settings(commonSettings("domains"))
  .dependsOn(commons)

lazy val commons = (project in file("modules/commons"))
  .settings(commonSettings("commons"))

lazy val infra = (project in file("modules/infra"))
  .settings(commonSettings("infra"))
  .settings(libraryDependencies ++= ("io.monix" %% "monix" % "3.0.0-RC2") +: databaseDependencies)
  .dependsOn(domains)

