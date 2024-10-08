/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, NoActiveSession}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.handlers.IvSessionDataRequestHandler
import uk.gov.hmrc.ivorchestration.model.api.ErrorResponses._
import uk.gov.hmrc.ivorchestration.model.api.{IvSessionData, IvSessionDataSearchRequest, IvSessionDataSearchResponse}
import uk.gov.hmrc.ivorchestration.model._
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepository
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class IvSessionDataController @Inject()(val authConnector: AuthConnector,
                                        headerValidator: HeaderValidator,
                                        ivSessionDataRepository: IvSessionDataRepository,
                                        cc: ControllerComponents)(implicit val ec: ExecutionContext)
  extends BackendController(cc) with AuthorisedFunctions with Logging {

  val requestsHandler = new IvSessionDataRequestHandler(ivSessionDataRepository)

  def ivSessionData(): Action[JsValue] =
    controllerAction(parse.json) { implicit request =>
      withJsonBody[IvSessionData] { ivSessionData =>
        withAuth(ivSessionData.journeyType) {
          requestsHandler.create(ivSessionData)
            .map(loc => Created.withHeaders("Location" -> loc))
        }
      }
    }

  def searchIvSessionData(): Action[JsValue] =
    headerValidator.validateAcceptHeader.async(parse.json) { implicit request =>
      withErrorHandling {
        request.body.asOpt[IvSessionDataSearchRequest] match {
          case None =>
            logger.warn(s"Missing IV session data search")
            Future.successful(BadRequest(Json.toJson(badRequest)))
          case Some(ivSessionDataSearch) =>
            requestsHandler.search(ivSessionDataSearch).flatMap { ivSessionData =>
              withAuth(ivSessionData.ivSessionData.journeyType) {
                Future.successful(Ok(Json.toJson(IvSessionDataSearchResponse.fromIvSessionDataCore(ivSessionData))))
              }
            }
          }
        }
      }

  protected def controllerAction[A](bodyParser: BodyParser[A])(block: Request[A] => Future[Result]): Action[A] =
    Action.async(bodyParser) { implicit request =>
      withErrorHandling {
        block(request)
      }
    }

  private def withErrorHandling(f: => Future[Result]): Future[Result] =
    f.recover {
      case _: NoActiveSession => Unauthorized(Json.toJson(unAuthorized))
      case RecordNotFound     => NotFound(Json.toJson(notFound))
      case CredIdForbidden    => Forbidden(Json.toJson(forbidden))
      case DatabaseError      => InternalServerError(Json.toJson(internalServerError))
      case _                  => InternalServerError(Json.toJson(internalServerError))
    }

  private def withAuth (journeyType: JourneyType)(f: => Future[Result])(implicit hc: HeaderCarrier): Future[Result] = {
    journeyType match {
      case StandaloneJourneyType => f
      case _ => authorised() {f}
    }
  }

}
