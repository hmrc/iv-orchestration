/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json._
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.ivorchestration.model.JourneyType
import uk.gov.hmrc.ivorchestration.model.core.CredId

case class IvSessionData(credId: Option[CredId],
                         nino: Option[String],
                         confidenceLevel: Int,
                         loginTimes: Option[DateTime],
                         credentialStrength: Option[String],
                         postCode: Option[String],
                         firstName: Option[String],
                         lastName: Option[String],
                         dateOfBirth: Option[LocalDate],
                         affinityGroup : Option[AffinityGroup],
                         ivFailureReason: Option[String],
                         evidencesPassedCount: Option[Int],
                         journeyType: JourneyType)

object IvSessionData {

  implicit val dateTimeFormat: Format[DateTime] = Format[DateTime](
    JodaReads.jodaDateReads("yyyy-MM-dd"),
    JodaWrites.jodaDateWrites("yyyy-MM-dd")
  )

  implicit val localDateFormat: Format[LocalDate] = Format[LocalDate](
    JodaReads.jodaLocalDateReads("yyyy-MM-dd"),
    JodaWrites.jodaLocalDateWrites("yyyy-MM-dd")
  )

  implicit val format: OFormat[IvSessionData] = Json.format[IvSessionData]

}
