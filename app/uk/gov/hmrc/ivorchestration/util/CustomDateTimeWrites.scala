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

package uk.gov.hmrc.ivorchestration.util

import play.api.libs.json._
import java.time.{OffsetDateTime, ZonedDateTime}

trait CustomDateTimeWrites {

  /**
   * ZonedDateTime is the Java time equivalent of Joda time's DateTime
   */
  def customZonedDateTimeWrites: Writes[ZonedDateTime] = new Writes[ZonedDateTime]{

    def writes(zdt: ZonedDateTime): JsValue = JsString(zdt.toLocalDate.toString)

  }

  /**
   * Use OffsetDateTime as "toInstant" method of ZonedDateTime does not seem to be recognized
   */
  def customMongoZonedDateTimeWrites: Writes[OffsetDateTime] = new Writes[OffsetDateTime]{

    def writes(offsetDateTime: OffsetDateTime): JsValue =
      Json.obj("$date" -> Json.obj("$numberLong" -> JsString(offsetDateTime.toInstant.toEpochMilli.toString)))

  }

}
