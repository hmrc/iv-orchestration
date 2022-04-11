/*
 * Copyright 2022 HM Revenue & Customs
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

import pureconfig.ConfigSource
import pureconfig.generic.auto._  // Do not remove this

trait AppConfiguration extends MongoConfiguration with AuthServiceConfiguration with ApiDocumentationConfiguration {

  lazy val auditingEnabled: Boolean =
    ConfigSource.default.at("auditing.enabled").loadOrThrow[Boolean]

  lazy val graphiteHost: String =
    ConfigSource.default.at("microservice.metrics.graphite.host").loadOrThrow[String]
}

trait MongoConfiguration {
  lazy val mongoConfig: MongoConfig =
    ConfigSource.default.at("mongodb").loadOrThrow[MongoConfig]
}

trait AuthServiceConfiguration {
  lazy val authConf: AuthServiceConf =
    ConfigSource.default.at("microservice.services.auth").loadOrThrow[AuthServiceConf]
}

trait ApiDocumentationConfiguration {
  lazy val apiConf: DocumentationConf =
    ConfigSource.default.at("api").loadOrThrow[DocumentationConf]
}

case class AuthServiceConf(protocol: String, host: String, port: Int)

case class MongoConfig(uri: String, ttl: Int, monitorRefresh: Int, failover: String, replaceIndexes: Boolean)

case class DocumentationConf(access: String, context: String)
