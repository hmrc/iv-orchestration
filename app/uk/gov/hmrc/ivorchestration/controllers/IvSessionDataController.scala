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
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.auth.core.{AuthorisedFunctions, NoActiveSession}
import uk.gov.hmrc.ivorchestration.config.MongoDBClient
import uk.gov.hmrc.ivorchestration.connectors.AuthConnector
import uk.gov.hmrc.ivorchestration.handlers.IvSessionDataRequestHandler
import uk.gov.hmrc.ivorchestration.model.api.{IvSessionData, IvSessionDataSearchRequest}
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepository
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.olegpy.meow.hierarchy._
import uk.gov.hmrc.auth.core
import uk.gov.hmrc.ivorchestration.model.{DatabaseError, RecordNotFound}

@Singleton()
class IvSessionDataController @Inject()(val authConnector: AuthConnector, cc: ControllerComponents)
  extends BackendController(cc) {

  val requestsHandler =
    new IvSessionDataRequestHandler[Future](new IvSessionDataRepository(new MongoDBClient {}.dbConnector))

  val authorisedFunctions = new AuthorisedFunctions {
    override def authConnector: core.AuthConnector = authConnector
  }

  def ivSessionData(): Action[JsValue] = controllerAction(parse.json) {
    implicit request =>
      withJsonBody[IvSessionData] { ivSessionData =>
          requestsHandler.create(ivSessionData)
            .map(loc => Created.withHeaders("Location" -> loc))
      }
  }

  def searchIvSessionData(): Action[JsValue] = controllerAction(parse.json) {
    implicit request =>
      withJsonBody[IvSessionDataSearchRequest] { ivSessionDataSearch =>
        requestsHandler.search(ivSessionDataSearch).map { ivSessionData => Ok(Json.toJson(ivSessionData))
        }
      }
    }

  protected def controllerAction[A](bodyParser: BodyParser[A])(block: Request[A] => Future[Result]): Action[A] =
    Action.async(bodyParser) {
      implicit request =>
        withErrorHandling {
          authorisedFunctions.authorised() {
            block(request)
          }
        }
    }

  private def withErrorHandling(f: => Future[Result]): Future[Result] =
    f.recover {
      case _: NoActiveSession => Unauthorized
      case RecordNotFound     => NotFound
      case DatabaseError      => InternalServerError
      case _                  => InternalServerError
    }
}