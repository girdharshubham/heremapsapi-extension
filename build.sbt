name := "heremapsapi-speed-alerts"

version := "0.1"

scalaVersion := "2.12.13"
val TypesafeConfigVersion = "1.4.1"
val AkkaVersion = "2.6.13"
val AkkaHttpVersion = "10.2.4"
val PlayJsonVersion = "2.9.2"
val ScalazVersion = "7.3.3"
val LogbackVersion = "1.2.3"
val TypesafeLoggingVersion = "3.9.2"

lazy val typesafeConfig = "com.typesafe" % "config" % TypesafeConfigVersion
lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % AkkaVersion
lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % AkkaVersion
lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
lazy val playJson = "com.typesafe.play" %% "play-json" % PlayJsonVersion
lazy val scalaz = "org.scalaz" %% "scalaz-core" % ScalazVersion
lazy val spray = "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
lazy val logback = "ch.qos.logback" % "logback-classic" % LogbackVersion
lazy val typesafeLogging = "com.typesafe.scala-logging" %% "scala-logging" % TypesafeLoggingVersion

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
      typesafeLogging
    )
  )
  .dependsOn(common)
