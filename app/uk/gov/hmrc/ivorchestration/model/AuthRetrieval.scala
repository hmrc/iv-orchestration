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

package uk.gov.hmrc.ivorchestration.model

import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import uk.gov.hmrc.auth.core.retrieve.ItmpAddress

object AuthRetrieval {

  implicit val dateTimeFormat: Format[DateTime] = Format[DateTime](JodaReads.jodaDateReads("yyyy-MM-dd"), JodaWrites.jodaDateWrites("yyyy-MM-dd"))

  implicit val localDateFormat: Format[LocalDate] = Format[LocalDate](JodaReads.jodaLocalDateReads("yyyy-MM-dd"), JodaWrites.jodaLocalDateWrites("yyyy-MM-dd"))

  implicit val itmpAddressFormat: Format[ItmpAddress] = Json.format[ItmpAddress]

  implicit val format = Json.format[AuthRetrieval]

  val dbKey: (String, String) => Seq[(String, JsValueWrapper)] =
    (journeyId, credId) => Seq("authRetrieval.journeyId.value" -> JsString(journeyId), "authRetrieval.credId" -> JsString(credId))
}

case class AuthRetrieval(
                          journeyId: JourneyId,
                          credId: String,
                          nino: Option[String],
                          confidenceLevel: Int,
                          loginTimes: Option[DateTime],
                          credentialStrength: Option[String],
                          itmpAddress: Option[ItmpAddress],
                          postCode: Option[String],
                          firstName: Option[String],
                          lastName: Option[String],
                          dateOfbirth: Option[LocalDate]
                        )

case class AuthRetrievalCore(authRetrieval: AuthRetrieval, createdAt: DateTime)

object AuthRetrievalCore {
  import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.dateTimeFormats

  implicit val format = Json.format[AuthRetrievalCore]
}


