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

import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import uk.gov.hmrc.ivorchestration.model.core.{CredId, JourneyId}

case class IvSessionData(
                          credId: CredId,
                          nino: Option[String],
                          confidenceLevel: Int,
                          loginTimes: Option[DateTime],
                          credentialStrength: Option[String],
                          postCode: Option[String],
                          firstName: Option[String],
                          lastName: Option[String],
                          dateOfBirth: Option[LocalDate]
                        )

object IvSessionData {

  implicit val dateTimeFormat: Format[DateTime] = Format[DateTime](JodaReads.jodaDateReads("yyyy-MM-dd"), JodaWrites.jodaDateWrites("yyyy-MM-dd"))

  implicit val localDateFormat: Format[LocalDate] = Format[LocalDate](JodaReads.jodaLocalDateReads("yyyy-MM-dd"), JodaWrites.jodaLocalDateWrites("yyyy-MM-dd"))

  implicit val format = Json.format[IvSessionData]

  val dbKey: (JourneyId, CredId) => Seq[(String, JsValueWrapper)] =
    (journeyId, credId) => Seq("journeyId" -> Json.toJson(journeyId), "ivSessionData.credId" -> Json.toJson(credId))
}