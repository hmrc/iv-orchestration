import sbt.Keys.*
import sbt.*
import scoverage.ScoverageKeys

val appName = "iv-orchestration"

ThisBuild / majorVersion := 2
ThisBuild / scalaVersion := "3.7.4"

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
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
      evictionWarningOptions  := EvictionWarningOptions.default.withWarnEvictionSummary(false)
  )
  .settings(
    PlayKeys.playDefaultPort := 9276,
    scoverageSettings,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= Seq(
      "-Wconf:msg=unused import&src=html/.*:s",
      "-Wconf:msg=Flag.*repeatedly:s",
      "-Wconf:src=routes/.*:s"
    )
  )
  .settings(Compile / unmanagedResourceDirectories += baseDirectory.value / "resources")

lazy val scoverageSettings =
  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 94,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )

Test / parallelExecution := true
Test / Keys.fork := true
Test / scalacOptions --= Seq("-Wdead-code", "-Wvalue-discard")
