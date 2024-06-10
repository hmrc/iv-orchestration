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

import java.time.{Instant, LocalDate, OffsetDateTime, ZonedDateTime, ZoneOffset}
import scala.util.{Failure, Success, Try}

trait CustomDateTimeReads {

  def customZonedDateTimeReads: Reads[ZonedDateTime] = new Reads[ZonedDateTime] {

    def reads(json: JsValue): JsResult[ZonedDateTime] = {

      json match {
        case JsString(s) =>
          Try(LocalDate.parse(s)) match {
            case Success(localDate) => JsSuccess(localDate.atStartOfDay(ZoneOffset.UTC))
            case Failure(_) => JsError(Seq(JsPath() -> Seq(JsonValidationError("error.invalid.date"))))
          }
        case _ => JsError(Seq(JsPath() -> Seq(JsonValidationError("error.invalid.data.type"))))
      }
    }

  }

  def customMongoOffsetDateTimeReads: Reads[OffsetDateTime] = new Reads[OffsetDateTime] {

    def reads(json: JsValue): JsResult[OffsetDateTime] = {

      (json \ "$date" \ "$numberLong").validate[String] match {
        case JsSuccess(s, _) =>
          Try(OffsetDateTime.ofInstant(Instant.ofEpochMilli(s.toLong), ZoneOffset.UTC)) match {
            case Success(offsetDateTime) => JsSuccess(offsetDateTime)
            case Failure(_) => JsError(Seq(JsPath() -> Seq(JsonValidationError("error.invalid.date.time"))))
          }
        case _ => JsError(Seq(JsPath() -> Seq(JsonValidationError("error.invalid.data.type"))))
      }
    }


  }

}
