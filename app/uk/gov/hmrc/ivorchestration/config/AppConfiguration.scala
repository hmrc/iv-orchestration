/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(val servicesConfig: ServicesConfig, appConfiguration: Configuration){

  lazy val authConf: String = servicesConfig.baseUrl("auth")

  lazy val access: String = appConfiguration.getOptional[String]("api.access").getOrElse("PRIVATE")
  lazy val context: String = appConfiguration.getOptional[String]("api.context").getOrElse("individuals/iv-orchestration")

  lazy val mongodbTTL: Int = appConfiguration.getOptional[Int]("mongodb.ttl").getOrElse(60)
  lazy val mongodbReplaceIndexes: Boolean = appConfiguration.getOptional[Boolean]("mongodb.replace-indexes").getOrElse(false)

  lazy val apiConf: DocumentationConf = DocumentationConf(access, context)
}

case class DocumentationConf(access: String, context: String)
