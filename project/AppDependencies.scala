import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc.mongo"          %% "hmrc-mongo-play-28"         % "1.2.0",
    "uk.gov.hmrc"                %% "bootstrap-backend-play-28"  % "7.15.0",
    "uk.gov.hmrc"                %% "play-hmrc-api"              % "7.2.0-play-28",
    "com.typesafe.play"          %% "play-json-joda"             % "2.9.4"
  )

  val test = Seq(
    "org.scalamock"           %% "scalamock"                % "5.2.0"                 % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0"                 % "test, it",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.35.10"               % "test, it"
  )
}