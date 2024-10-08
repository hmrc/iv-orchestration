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

package uk.gov.hmrc.ivorchestration.handlers

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ivorchestration.model.api.ErrorResponses

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

trait HeadersValidationHandler {

  val validateVersion: String => Boolean = v => v == "1.0" || v == "2.0"

  val validateContentType: String => Boolean = ct => ct == "json" || ct == "xml"

  val matchHeader: String => Option[Match] =
    new Regex( """^application/vnd[.]{1}hmrc[.]{1}(.*?)[+]{1}(.*)$""", "version", "contenttype") findFirstMatchIn _

  val acceptHeaderValidationRules: Option[String] => Boolean =
    _ flatMap (a => matchHeader(a) map {
      res => validateContentType(res.group("contenttype")) && validateVersion(res.group("version"))
    }) getOrElse false

  def validateRules(headers: Map[String, String]): Option[JsValue] =
    if (!acceptHeaderValidationRules(headers.get("Accept")))
      Some(Json.toJson(ErrorResponses.invalidHeaders))
    else
      None

}
