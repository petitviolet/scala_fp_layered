name := "scala_fp_layered"

val scala = "2.12.6"

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
  libraryDependencies ++= List(
    "org.scalaz" %% "scalaz-core" % "7.2.25"
  )
)

lazy val scala_fp_layered = (project in file("."))
  .settings(commonSettings("scala_fp_layered"))
  .aggregate(applications, controllers, domains, infra)


lazy val controllers = (project in file("modules/controllers"))
  .settings(commonSettings("controllers"))
  .dependsOn(applications, infra)

lazy val applications = (project in file("modules/applications"))
  .settings(commonSettings("applications"))
  .dependsOn(domains)

lazy val domains = (project in file("modules/domains"))
  .settings(commonSettings("domains"))

lazy val infra = (project in file("modules/infra"))
  .settings(commonSettings("infra"))
  .dependsOn(domains)

