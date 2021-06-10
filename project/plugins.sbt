resolvers += Resolver.bintrayRepo("hmrc", "releases")
resolvers += Resolver.url("HMRC Private Sbt Plugin Releases", url("https://artefacts.tax.service.gov.uk/artifactory/hmrc-sbt-plugin-releases-local"))(Resolver.ivyStylePatterns)
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)
resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")


addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "3.0.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "2.2.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "2.1.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.7")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

