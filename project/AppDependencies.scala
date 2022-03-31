import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc.mongo"          %% "hmrc-mongo-play-28"         % "0.62.0",
    "uk.gov.hmrc"                %% "bootstrap-backend-play-28"  % "5.2.0",
    "uk.gov.hmrc"                %% "play-hmrc-api"              % "6.4.0-play-28",
    "com.typesafe.play"          %% "play-json-joda"             % "2.6.7",
    "com.github.pureconfig"      %% "pureconfig"                 % "0.12.3",
    "org.typelevel"              %% "cats-core"                  % "2.1.1",
    "com.softwaremill.quicklens" %% "quicklens"                  % "1.4.12",
    "com.olegpy"                 %% "meow-mtl-core"              % "0.4.0"
  )

  val test = Seq(
    "org.scalamock"           %% "scalamock"                % "5.1.0"                 % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0"                 % "test, it",
    "com.vladsch.flexmark"    % "flexmark-all"              % "0.35.10"               % "test, it"
  )
}