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
import uk.gov.hmrc.ivorchestration.model.core.IvSessionDataCore
import uk.gov.hmrc.ivorchestration.util.{CustomDateTimeReads, CustomDateTimeWrites}

case class IvSessionDataSearchResponse(nino: Option[String],
                                       confidenceLevel: Int,
                                       loginTimes: Option[ZonedDateTime],
                                       credentialStrength: Option[String],
                                       postCode: Option[String],
                                       firstName: Option[String],
                                       lastName: Option[String],
                                       dateOfBirth: Option[LocalDate],
                                       affinityGroup : Option[AffinityGroup],
                                       ivFailureReason: Option[String],
                                       evidencesPassedCount: Option[Int])


object IvSessionDataSearchResponse extends CustomDateTimeReads with CustomDateTimeWrites {
  def fromIvSessionDataCore(ivSessionDataCore: IvSessionDataCore): IvSessionDataSearchResponse = {
    import ivSessionDataCore.ivSessionData._
    IvSessionDataSearchResponse(
      nino, confidenceLevel, loginTimes, credentialStrength, postCode,
      firstName, lastName, dateOfBirth, affinityGroup, ivFailureReason,
      evidencesPassedCount
    )
  }

  implicit val customZonedDateTimeFormat: Format[ZonedDateTime] = Format[ZonedDateTime](
    {json: JsValue => customZonedDateTimeReads.reads(json)},
    {zdt: ZonedDateTime => customZonedDateTimeWrites.writes(zdt)}
  )

  implicit val format = Json.format[IvSessionDataSearchResponse]
}
