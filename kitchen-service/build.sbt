val finchVersion = "0.32.1"
val circeVersion = "0.14.1"
val scalatestVersion = "3.2.9"

lazy val root = (project in file("."))
  .settings(
    organization := "br.usp",
    name := "kitchen-service",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finchx-core"  % finchVersion,
      "com.github.finagle" %% "finchx-circe"  % finchVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "org.scalatest"      %% "scalatest"    % scalatestVersion % "test",
      "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0",
      "org.apache.kafka" %% "kafka" % "2.8.0"
    )
  )