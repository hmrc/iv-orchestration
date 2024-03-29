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

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    majorVersion                     := 2,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
      evictionWarningOptions           := EvictionWarningOptions.default.withWarnEvictionSummary(false)
  )
  .configs(IntegrationTest)
  .settings(scalaVersion := "2.13.8")
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(playDefaultPort := 9276)
  .settings(coverageMinimumStmtTotal := 85)
  .settings(coverageFailOnMinimum := true)
  .settings(coverageExcludedPackages := excludedPackages.mkString(";"))
  .settings(unmanagedResourceDirectories in Compile += baseDirectory.value / "resources")
