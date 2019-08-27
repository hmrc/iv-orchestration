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

import cats.instances.future._
import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.ivorchestration.config.MongoDBClient
import uk.gov.hmrc.ivorchestration.handlers.AuthRetrievalRequestHandler
import uk.gov.hmrc.ivorchestration.model.AuthRetrieval
import uk.gov.hmrc.ivorchestration.services.AuthRetrievalDBService
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class AuthRetrievalController @Inject()(cc: ControllerComponents) extends BackendController(cc) with MongoDBClient {

  val requestsHandler = new AuthRetrievalRequestHandler[Future](new AuthRetrievalDBService(connector))

  def ivSessionData(): Action[AuthRetrieval] = Action.async(parse.json[AuthRetrieval]) {
    implicit request =>
      //TODO: Auth needs to be added here.
      requestsHandler.handleAuthRetrieval(request.body).map(authRetrieval => Created(Json.toJson(authRetrieval)))
  }
}