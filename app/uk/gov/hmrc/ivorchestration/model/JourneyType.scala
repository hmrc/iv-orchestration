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

package uk.gov.hmrc.ivorchestration.model

import play.api.libs.json.{Format, JsResult, JsString, JsSuccess, JsValue, Reads, Writes}

sealed trait JourneyType
case object UpliftJourneyType extends JourneyType
case object StandaloneJourneyType extends JourneyType
case object UnknownJourneyType extends JourneyType

object JourneyType {
  val writes: Writes[JourneyType] = new Writes[JourneyType] {
    override def writes(o: JourneyType): JsValue = o match {
      case UpliftJourneyType => JsString("uplift")
      case StandaloneJourneyType => JsString("standalone")
      case _ => JsString("unknown")
    }
  }

  val reads: Reads[JourneyType] = new Reads[JourneyType] {
    override def reads(json: JsValue): JsResult[JourneyType] = {
      json.validate[String].flatMap {
        case "uplift" => JsSuccess(UpliftJourneyType)
        case "standalone" => JsSuccess(StandaloneJourneyType)
        case _ => JsSuccess(UnknownJourneyType)
      }
    }
  }

  implicit val format: Format[JourneyType] = Format(reads, writes)
}
