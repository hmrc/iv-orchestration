import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootStrapVersion: String = "8.5.0"
  val mongoVersion: String = "1.9.0"

  val compile = Seq(
    "uk.gov.hmrc.mongo"          %% "hmrc-mongo-play-30"         % mongoVersion,
    "uk.gov.hmrc"                %% "bootstrap-backend-play-30"  % bootStrapVersion,
    "uk.gov.hmrc"                %% "play-hmrc-api-play-30"      % "8.0.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootStrapVersion        % Test,
    "org.scalamock"           %% "scalamock"                % "6.0.0"                 % Test,
    "org.playframework"       %% "play-test"                % current                 % Test,
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % Test,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "7.0.1"                 % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.64.8"                % Test
  )
}