import Dependencies._

name := "heremapsapi-speed-alerts"

version := "0.1"

scalaVersion := "2.12.13"


lazy val common = (project in file("common"))
  .settings(
    libraryDependencies ++= Seq(
      mongoDbDriver,
      akkaActor
    )
  )

lazy val impl = (project in file("impl"))
  .settings(
    libraryDependencies ++= Seq(
      typesafeConfig,
      akkaStream,
      akkaHttp,
      scalaz,
      spray,
      logback,
      typesafeLogging,
      mongoDbDriver % "provided"
    )
  )
  .dependsOn(common)
