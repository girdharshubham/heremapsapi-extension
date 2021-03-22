import Dependencies._

name := "heremapsapi-speed-alerts"

version := "0.1"

scalaVersion := "2.12.13"


lazy val common = (project in file("common"))
  .settings(
    libraryDependencies ++= Seq(
      playJson
    )
  )

lazy val impl = (project in file("impl"))
  .settings(
    libraryDependencies ++= Seq(
      typesafeConfig,
      akkaActor,
      akkaStream,
      akkaHttp,
      playJson,
      scalaz,
      spray,
      logback,
      typesafeLogging,
      mongoDbDriver
    )
  )
  .dependsOn(common)
