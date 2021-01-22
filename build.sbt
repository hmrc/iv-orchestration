import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import play.sbt.PlayImport.PlayKeys.playDefaultPort
import sbt.Keys._
import sbt._

val appName = "iv-orchestration"


val excludedPackages = Seq(
  "<empty>",
  ".*Reverse.*",
  ".*Routes*.*",
  ".*standardError*.*",
  "uk.gov.hmrc.BuildInfo",
  ".*models*.*",
  ".*AuthConnector*.*",
  "..*config*.*",
  ".*testOnlyDoNotUseInAppConf*.*",
  "testOnly.*")

val silencerVersion="1.7.0"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    majorVersion                     := 2,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
      evictionWarningOptions           := EvictionWarningOptions.default.withWarnEvictionSummary(false)
  )
  .settings(SilencerSettings())
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(scalaVersion := "2.12.11")
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(playDefaultPort := 9276)
  .settings(coverageMinimum := 85)
  .settings(coverageFailOnMinimum := true)
  .settings(coverageExcludedPackages := excludedPackages.mkString(";"))
  .settings(unmanagedResourceDirectories in Compile += baseDirectory.value / "resources")

