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

package uk.gov.hmrc.ivorchestration.model.api

import play.api.libs.json.{Format, Json}

case class ErrorResult(code: String, message: String)

object ErrorResult {
  implicit val format: Format[ErrorResult] = Json.format
}

object ErrorResponses {

  val internalServerError = ErrorResult("INTERNAL_SERVER_ERROR", "Something went wrong on our side - come back later")
  val notFound = ErrorResult("RECORD_NOT_FOUND", "The record could not be found")
  val unAuthorized = ErrorResult("UNAUTHORIZED", "User unauthorized")
  val badRequest = ErrorResult("BAD_REQUEST", "The provided body is invalid.")
}