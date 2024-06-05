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
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion                     := 2,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
      evictionWarningOptions           := EvictionWarningOptions.default.withWarnEvictionSummary(false)
  )
  .settings(scalaVersion := "2.13.12")
  .settings(scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s")
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(playDefaultPort := 9276)
  .settings(coverageMinimumStmtTotal := 85)
  .settings(coverageFailOnMinimum := true)
  .settings(coverageExcludedPackages := excludedPackages.mkString(";"))
  .settings(Compile / unmanagedResourceDirectories += baseDirectory.value / "resources")
