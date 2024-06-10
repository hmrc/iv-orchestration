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

package uk.gov.hmrc.ivorchestration.model.api

import java.time.{LocalDate, ZonedDateTime}
import play.api.libs.json._
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.ivorchestration.model.JourneyType
import uk.gov.hmrc.ivorchestration.model.core.CredId
import uk.gov.hmrc.ivorchestration.util.{CustomDateTimeReads, CustomDateTimeWrites}


case class IvSessionData(credId: Option[CredId],
                         nino: Option[String],
                         confidenceLevel: Int,
                         loginTimes: Option[ZonedDateTime],
                         credentialStrength: Option[String],
                         postCode: Option[String],
                         firstName: Option[String],
                         lastName: Option[String],
                         dateOfBirth: Option[LocalDate],
                         affinityGroup : Option[AffinityGroup],
                         ivFailureReason: Option[String],
                         evidencesPassedCount: Option[Int],
                         journeyType: JourneyType)

object IvSessionData extends CustomDateTimeReads with CustomDateTimeWrites {

  implicit val customZonedDateTimeFormat: Format[ZonedDateTime] = Format[ZonedDateTime](
    {json: JsValue => customZonedDateTimeReads.reads(json)},
    {zdt: ZonedDateTime => customZonedDateTimeWrites.writes(zdt)}
  )

  implicit val format: OFormat[IvSessionData] = Json.format[IvSessionData]

}
