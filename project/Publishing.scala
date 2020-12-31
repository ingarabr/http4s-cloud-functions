import bintray.BintrayPlugin
import bintray.BintrayKeys._
import sbt.{Def, _}
import sbt.Keys._

object Publishing extends AutoPlugin {

  override def trigger = AllRequirements

  override def requires: Plugins = BintrayPlugin

  override def buildSettings: Seq[Def.Setting[_]] =
    Seq(
      licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
      version := GitTagVersion.versionFromGit,
      bintrayReleaseOnPublish := GitTagVersion.isSnapShot(version.value),
      bintrayVcsUrl := Some("git@github.com:ingarabr/http4s-cloud-functions.git")
    )
  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      Test / publishArtifact := false,
      bintrayRepository := "oss"
    )
}
