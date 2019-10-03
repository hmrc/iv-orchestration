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

package uk.gov.hmrc.ivorchestration.controllers

import controllers.Assets
import javax.inject.{Inject, Singleton}
import play.api.http.HttpErrorHandler
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.api.controllers.DocumentationController
import uk.gov.hmrc.ivorchestration.config.ApiDocumentationConfiguration

@Singleton
class IvOrchestrationDocumentationController @Inject()(assets: Assets, cc: ControllerComponents, errorHandler: HttpErrorHandler)
  extends DocumentationController(cc, assets, errorHandler) with ApiDocumentationConfiguration {

  override def definition(): Action[AnyContent] = Action {
    Ok(uk.gov.hmrc.ivorchestration.views.txt.definition(apiConf))
  }

  override def conf(version: String, file: String): Action[AnyContent] = {
    assets.at(s"/public/api/conf/$version", file)
  }
}
