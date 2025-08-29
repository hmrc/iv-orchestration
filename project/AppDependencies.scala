import play.core.PlayVersion.current
import sbt.*

object AppDependencies {

  val bootStrapVersion: String = "9.14.0"
  val mongoVersion: String = "2.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"          %% "hmrc-mongo-play-30"         % mongoVersion,
    "uk.gov.hmrc"                %% "bootstrap-backend-play-30"  % bootStrapVersion,
    "uk.gov.hmrc"                %% "play-hmrc-api-play-30"      % "8.0.0" // Version 8.1.0+ requires play 3.0+
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootStrapVersion,
    "org.scalamock"           %% "scalamock"                % "7.4.1",
    "org.playframework"       %% "play-test"                % current,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "7.0.2",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.64.8"
  ).map(_ % "test")
}