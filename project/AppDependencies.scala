import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"                %% "simple-reactivemongo"       % "7.31.0-play-27",
    "uk.gov.hmrc"                %% "bootstrap-backend-play-27"  % "3.2.0",
    "uk.gov.hmrc"                %% "play-hmrc-api"              % "4.1.0-play-26",
    "com.typesafe.play"          %% "play-json-joda"             % "2.6.7",
    "com.github.pureconfig"      %% "pureconfig"                 % "0.12.3",
    "org.typelevel"              %% "cats-core"                  % "2.1.1",
    "com.softwaremill.quicklens" %% "quicklens"                  % "1.4.12",
    "com.olegpy"                 %% "meow-mtl-core"              % "0.4.0"
  )

  val test = Seq(
    "org.scalamock"           %% "scalamock"                % "4.4.0"                 % "test",
    "com.typesafe.play"       %% "play-test"                % current                 % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "4.0.3"                 % "test, it"
  )
}