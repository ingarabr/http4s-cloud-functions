import Versions.V

inThisBuild(
  Seq(
    organization := "com.github.ingarabr",
    scalaVersion := V.scala.default,
    crossScalaVersions := V.scala.cross,
    homepage := Some(url("https://github.com/ingarabr/http4s-cloud-functions")),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    developers := List(
      Developer(
        "ingarabr",
        "Ingar Abrahamsen",
        "ingar.abrahamasen@gmail.com",
        url("https://github.com/ingarabr/")
      )
    )
  )
)

lazy val root = (project in file("."))
  .settings(name := "http4s-cloud-functions-root")
  .enablePlugins(NoPublish)
  .settings(crossScalaVersions := Nil)
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
