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

import play.api.libs.json._
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.ivorchestration.model.UpliftJourneyType
import uk.gov.hmrc.ivorchestration.model.api.IvSessionData.format
import uk.gov.hmrc.ivorchestration.model.core.CredId
import uk.gov.hmrc.ivorchestration.testsuite.BaseSpec

import java.time.{LocalDate, ZonedDateTime}
import java.time.format.DateTimeFormatter

class IvSessionDataSpec extends BaseSpec {

  val loginTimesDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  val credId: String = "Test-credId"
  val nino: String = "AA000003D"
  val confidenceLevel: Int = 250
  val validLoginTimes: String = "2024-05-30"
  val invalidLoginTimes: String = "200-05-31"
  val credentialStrength: String = "strong"
  val postCode: String = "TF3 4NT"
  val firstName: String = "Jim"
  val lastName: String = "Ferguson"
  val validDateOfBirth: String = "1987-02-20"
  val invalidDateOfBirth: String = "10-10-10"
  val affinityGroup: String = "Individual"
  val ivFailureReason: String = "Failure"
  val evidencesPassedCount: Int = 5
  val journeyType: String = "uplift"

  val loginTimesInZonedDateTimeFormat: String = "2024-05-30T00:00:00+00:00"

  val expectedLoginTimes: ZonedDateTime = ZonedDateTime.parse(loginTimesInZonedDateTimeFormat, loginTimesDateFormatter)
  val expectedDateOfBirth: LocalDate = LocalDate.parse(validDateOfBirth)

  def getIvSessionData(
                        credId: String,
                        nino: String,
                        confidenceLevel: Int,
                        loginTimes: String,
                        credentialStrength: String,
                        postCode: String,
                        firstName: String,
                        lastName: String,
                        dateOfBirth: String,
                        affinityGroup: String,
                        ivFailureReason : String,
                        evidencesPassedCount: Int,
                        journeyType: String): String =
    s"""|{
        | "credId": "$credId",
        | "nino" : "$nino",
        | "confidenceLevel" : $confidenceLevel,
        | "loginTimes" : "$loginTimes",
        | "credentialStrength": "$credentialStrength",
        | "postCode" : "$postCode",
        | "firstName" : "$firstName",
        | "lastName" : "$lastName",
        | "dateOfBirth" : "$dateOfBirth",
        | "affinityGroup" : "$affinityGroup",
        | "ivFailureReason" : "$ivFailureReason",
        | "evidencesPassedCount" : $evidencesPassedCount,
        | "journeyType" : "$journeyType"
        |}""".stripMargin

  def getMinimalIvSessionData(confidenceLevel: Int, journeyType: String): String =
    s"""|{
        |  "confidenceLevel" : $confidenceLevel,
        |  "journeyType" : "$journeyType"
        |}""".stripMargin

  def getIvSessionDataWithInvalidLoginTimesType(confidenceLevel: Int, journeyType: String): String =
    s"""|{
        |  "confidenceLevel" : $confidenceLevel,
        |  "loginTimes" : 1,
        |  "journeyType" : "$journeyType"
        |}""".stripMargin

  def removeWhiteSpace(s: String): String = s.replace("\n", "").replace(" ", "")

  "IvSessionData" should {

    "deserialize a fully populated representation of IvSessionData" in {

      val ivSessionAsString: String = getIvSessionData(
        credId,
        nino,
        confidenceLevel,
        validLoginTimes,
        credentialStrength,
        postCode,
        firstName,
        lastName,
        validDateOfBirth,
        affinityGroup,
        ivFailureReason,
        evidencesPassedCount,
        journeyType)

      val expectedIvSessionData: IvSessionData =  new IvSessionData(
        credId = Some(CredId(credId)),
        nino = Some(nino),
        confidenceLevel = confidenceLevel,
        loginTimes = Some(expectedLoginTimes),
        credentialStrength = Some(credentialStrength),
        postCode = Some(postCode),
        firstName = Some(firstName),
        lastName = Some(lastName),
        dateOfBirth = Some(expectedDateOfBirth),
        affinityGroup = Some(AffinityGroup.Individual),
        ivFailureReason = Some(ivFailureReason),
        evidencesPassedCount = Some(evidencesPassedCount),
        journeyType = UpliftJourneyType
      )

      Json.parse(ivSessionAsString).validate[IvSessionData] match {
        case JsSuccess(ivSessionData, _) => ivSessionData mustBe expectedIvSessionData
        case JsError(error) => fail(s"Error occurred deserializing IvSessionData : $error")
      }
    }

    "deserialize a minimally populated instance of IvSessionData" in {

      val ivSessionAsString: String = getMinimalIvSessionData(confidenceLevel, journeyType)

      val expectedIvSessionData: IvSessionData =  new IvSessionData(
        credId = None,
        nino = None,
        confidenceLevel = confidenceLevel,
        loginTimes = None,
        credentialStrength = None,
        postCode = None,
        firstName = None,
        lastName = None,
        dateOfBirth = None,
        affinityGroup = None,
        ivFailureReason = None,
        evidencesPassedCount = None,
        journeyType = UpliftJourneyType
      )

      Json.parse(ivSessionAsString).validate[IvSessionData] match {
        case JsSuccess(ivSessionData, _) => ivSessionData mustBe expectedIvSessionData
        case JsError(error) => fail(s"Error occurred deserializing IvSessionData : $error")
      }

    }

    "raise an error when deserializing an instance of IvSessionData with invalid login times" in {

      val ivSessionAsString: String = getIvSessionData(
        credId,
        nino,
        confidenceLevel,
        invalidLoginTimes,
        credentialStrength,
        postCode,
        firstName,
        lastName,
        validDateOfBirth,
        affinityGroup,
        ivFailureReason,
        evidencesPassedCount,
        journeyType)

      Json.parse(ivSessionAsString).validate[IvSessionData] match {
        case JsSuccess(_, _) => fail("An instance of IvSessionData should not be created when the loginTimes is invalid")
        case JsError(errors) => errors.head._2.head.messages.head mustBe "error.invalid.date"
      }

    }

    "raise an error when deserializing an instance of IvSessionData with invalid login times type" in {

      val ivSessionAsString: String = getIvSessionDataWithInvalidLoginTimesType(confidenceLevel, journeyType)

      Json.parse(ivSessionAsString).validate[IvSessionData] match {
        case JsSuccess(_, _) => fail("An instance of IvSessionData should not be created when the loginTimes type is invalid")
        case JsError(errors) => errors.head._2.head.messages.head mustBe "error.invalid.data.type"
      }

    }

    "raise an error when deserializing an instance of IvSessionData with an invalid date of birth" in {

      val ivSessionAsString: String = getIvSessionData(
        credId,
        nino,
        confidenceLevel,
        validLoginTimes,
        credentialStrength,
        postCode,
        firstName,
        lastName,
        invalidDateOfBirth,
        affinityGroup,
        ivFailureReason,
        evidencesPassedCount,
        journeyType)

      Json.parse(ivSessionAsString).validate[IvSessionData] match {
        case JsSuccess(_, _) => fail("An instance of IvSessionData should not be created when the date of birth is invalid")
        case JsError(errors) => errors.head._1.path.head.toJsonString mustBe ".dateOfBirth"
      }

    }

    "serialize a fully populated instance of IvSessionData" in {

      val ivSessionData: IvSessionData =  IvSessionData(
        credId = Some(CredId(credId)),
        nino = Some(nino),
        confidenceLevel = confidenceLevel,
        loginTimes = Some(expectedLoginTimes),
        credentialStrength = Some(credentialStrength),
        postCode = Some(postCode),
        firstName = Some(firstName),
        lastName = Some(lastName),
        dateOfBirth = Some(expectedDateOfBirth),
        affinityGroup = Some(AffinityGroup.Individual),
        ivFailureReason = Some(ivFailureReason),
        evidencesPassedCount = Some(evidencesPassedCount),
        journeyType = UpliftJourneyType
      )

      val expectedIvSessionAsString: String = getIvSessionData(
        credId,
        nino,
        confidenceLevel,
        validLoginTimes,
        credentialStrength,
        postCode,
        firstName,
        lastName,
        validDateOfBirth,
        affinityGroup,
        ivFailureReason,
        evidencesPassedCount,
        journeyType)

      val serializedIvSessionData: String = Json.toJson(ivSessionData).toString

      removeWhiteSpace(serializedIvSessionData) mustBe removeWhiteSpace(expectedIvSessionAsString)

    }

    "serialize a minimally populated instance of IvSessionData" in {

      val ivSessionData: IvSessionData =  IvSessionData(
        credId = None,
        nino = None,
        confidenceLevel = confidenceLevel,
        loginTimes = None,
        credentialStrength = None,
        postCode = None,
        firstName = None,
        lastName = None,
        dateOfBirth = None,
        affinityGroup = None,
        ivFailureReason = None,
        evidencesPassedCount = None,
        journeyType = UpliftJourneyType
      )

      val expectedIvSessionDataAsString: String = getMinimalIvSessionData(confidenceLevel, journeyType)

      val serializedIvSessionData: String = Json.toJson(ivSessionData).toString

      removeWhiteSpace(serializedIvSessionData) mustBe removeWhiteSpace((expectedIvSessionDataAsString))

    }
  }

}
