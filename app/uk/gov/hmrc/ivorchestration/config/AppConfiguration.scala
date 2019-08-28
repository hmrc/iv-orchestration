/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ivorchestration.config

trait AppConfiguration extends MongoConfiguration {

  import pureconfig.generic.auto._  //Do not remove this

  lazy val authBaseUrl: AuthService = pureconfig.loadConfigOrThrow[AuthService]("microservice.services.auth")
  lazy val auditingEnabled: Boolean = pureconfig.loadConfigOrThrow[Boolean]("auditing.enabled")
  lazy val graphiteHost: String     = pureconfig.loadConfigOrThrow[String]("microservice.metrics.graphite.host")
}

trait MongoConfiguration {
  import pureconfig.generic.auto._

  lazy val mongoConfig: MongoConfig = pureconfig.loadConfigOrThrow[MongoConfig]("mongodb")
}


case class AuthService(host: String, port: Int)

case class MongoConfig(uri: String, ttl: Int, monitorRefresh: Int, failover: String)
