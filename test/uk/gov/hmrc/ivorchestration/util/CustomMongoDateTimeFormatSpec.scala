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

import java.time.{Instant, OffsetDateTime, ZoneOffset}
import play.api.libs.json._
import uk.gov.hmrc.ivorchestration.model.core.IvSessionDataCore.customMongoDateTimeFormat
import uk.gov.hmrc.ivorchestration.testsuite.BaseSpec

class CustomMongoDateTimeFormatSpec extends BaseSpec {

  val currentTime: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)

  val currentTimeInMillis: Long = currentTime.toInstant.toEpochMilli

  val truncatedCurrentTime: OffsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(currentTimeInMillis), ZoneOffset.UTC)

  val invalidOffsetDateTime: String = "XXX"

  def getOffsetDateTimeAsStr(timeInMills: String): String =
    s"""|{
       |  "$$date" : {
       |    "$$numberLong" : "$timeInMills"
       |  }
       |}""".stripMargin

  def getInvalidOffsetDateTimeAsStr(timeInMillis: Long): String =
    s"""|{
        |  "$$date" : {
        |    "$$numberLong" : $timeInMillis
        |  }
        |}""".stripMargin

  "Mongo custom OffsetDateTime formatter" should {

    "deserialize a valid instance of OffsetDateTime value" in {

      val jsonString: String = getOffsetDateTimeAsStr(currentTimeInMillis.toString)

      Json.parse(jsonString).validate[OffsetDateTime] match {
        case JsSuccess(offsetDateTime, _) => offsetDateTime mustBe truncatedCurrentTime
        case JsError(error) => fail(s"An error occurred parsing the input Json : $error")
      }

    }

    "raise an error when the value for OffsetDateTime is invalid" in {

      val jsonString: String = getOffsetDateTimeAsStr(invalidOffsetDateTime)

      Json.parse(jsonString).validate[OffsetDateTime] match {
        case JsSuccess(_, _) => fail("Error an instance of OffsetDateTime should not be generated for an invalid date time value")
        case JsError(errors) => errors.head._2.head.messages.head mustBe "error.invalid.date.time"
      }

    }

    "raise an error when the value for OffsetDateTime is of the wrong type" in {

      val jsonString: String = getInvalidOffsetDateTimeAsStr(currentTimeInMillis)

      Json.parse(jsonString).validate[OffsetDateTime] match {
        case JsSuccess(_, _) => fail("Error : An instance of OffsetDateTime should not be generated for value of incorrect type")
        case JsError(errors) => errors.head._2.head.messages.head mustBe "error.invalid.data.type"
      }

    }

    "serialize an instance of OffsetDateTime to a precision of milliseconds" in {

      val actual: String  = Json.toJson(currentTime).toString()

      val expected: String = getOffsetDateTimeAsStr(currentTimeInMillis.toString).
        replace("\n", "").replace(" ", "")

      actual mustBe expected
    }
  }

}
