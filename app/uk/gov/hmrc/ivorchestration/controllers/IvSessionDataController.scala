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
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.auth.core.{AuthorisedFunctions, NoActiveSession}
import uk.gov.hmrc.ivorchestration.config.MongoDBClient
import uk.gov.hmrc.ivorchestration.connectors.AuthConnector
import uk.gov.hmrc.ivorchestration.handlers.IvSessionDataRequestHandler
import uk.gov.hmrc.ivorchestration.model.api.IvSessionData
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepositoryDBService
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class IvSessionDataController @Inject()(val authConnector: AuthConnector, cc: ControllerComponents)
  extends BackendController(cc) with MongoDBClient with AuthorisedFunctions {

  val requestsHandler = new IvSessionDataRequestHandler[Future](new IvSessionDataRepositoryDBService(dbConnector))

  def ivSessionData(): Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      withJsonBody[IvSessionData] {
        ivSessionData =>
          authorised() {
            requestsHandler.handle(ivSessionData, request.headers.toSimpleMap).map(loc => Created.withHeaders("Location" -> loc))
          }.recoverWith {
            case _: NoActiveSession => Future.successful(Unauthorized)
            case _                  => Future.successful(InternalServerError)
          }
      }
  }
}