/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json._
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.ivorchestration.model.core.IvSessionDataCore

import java.time.{LocalDate, LocalTime}

case class IvSessionDataSearchResponse(nino: Option[String],
                                       confidenceLevel: Int,
                                       loginTimes: Option[LocalTime],
                                       credentialStrength: Option[String],
                                       postCode: Option[String],
                                       firstName: Option[String],
                                       lastName: Option[String],
                                       dateOfBirth: Option[LocalDate],
                                       affinityGroup : Option[AffinityGroup],
                                       ivFailureReason: Option[String],
                                       evidencesPassedCount: Option[Int])


object IvSessionDataSearchResponse {
  def fromIvSessionDataCore(ivSessionDataCore: IvSessionDataCore): IvSessionDataSearchResponse = {
    import ivSessionDataCore.ivSessionData._
    IvSessionDataSearchResponse(
      nino, confidenceLevel, loginTimes, credentialStrength, postCode,
      firstName, lastName, dateOfBirth, affinityGroup, ivFailureReason,
      evidencesPassedCount
    )
  }

  implicit val dateTimeFormat: Format[LocalTime] = new Format[LocalTime] {
    override def reads(json: JsValue): JsResult[LocalTime] =
      json.validate[String].map(LocalTime.parse)

    override def writes(o: LocalTime): JsValue = Json.toJson(o.toString)
  }


  implicit val localDateFormat: Format[LocalDate] = new Format[LocalDate] {
    override def reads(json: JsValue): JsResult[LocalDate] =
      json.validate[String].map(LocalDate.parse)

    override def writes(o: LocalDate): JsValue = Json.toJson(o.toString)
  }

  implicit val format = Json.format[IvSessionDataSearchResponse]
}
