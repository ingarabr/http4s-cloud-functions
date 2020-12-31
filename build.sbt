import Versions.V

ThisBuild / organization := "com.github.ingarabr"
ThisBuild / scalaVersion := V.defaultScala

lazy val root = (project in file("."))
  .settings(name := "http4s-cloud-functions-root")
  .enablePlugins(NoPublish)
  .aggregate(
    `http4s-cloud-functions`,
    docs
  )

lazy val `http4s-cloud-functions` = (project in file("modules/http4s-cloud-functions"))
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % V.http4s,
      "com.google.cloud.functions" % "functions-framework-api" % V.cloudFunctions
    )
  )

lazy val docs = (project in file("project-docs"))
  .dependsOn(`http4s-cloud-functions`)
  .enablePlugins(MdocPlugin, NoPublish)
