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

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.ivorchestration.handlers.HeadersValidationHandler
import uk.gov.hmrc.ivorchestration.model.api.ErrorResponses

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HeaderValidator @Inject()(cc: ControllerComponents) extends Results with HeadersValidationHandler {

  def validateAction(rules: Option[String] => Boolean) = {
    new ActionBuilder[Request, AnyContent] with ActionFilter[Request] {

      override val parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser
      override protected val executionContext: ExecutionContext = cc.executionContext

      def filter[T](input: Request[T]): Future[Option[Result]] = Future.successful {
        implicit val r = input

        if (!rules(input.headers.get("Accept")))
          Some(NotAcceptable(Json.toJson(ErrorResponses.invalidHeaders)))
        else
          None
      }

    }
  }

  val validateAcceptHeader: ActionBuilder[Request, AnyContent] = validateAction(acceptHeaderValidationRules)
}
