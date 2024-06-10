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

package uk.gov.hmrc.ivorchestration.handlers

import play.api.libs.json.Json
import uk.gov.hmrc.ivorchestration.testsuite.BaseSpec

class HeadersValidationHandlerSpec extends BaseSpec {


  "validation succeed if version is 1.0 or 2.0" in new HeadersValidationHandler {
    validateVersion("1.0") mustBe true
    validateVersion("2.0") mustBe true
    validateVersion("3.0") mustBe false
  }

  "validation succeed if file extension contain json or xml" in new HeadersValidationHandler {
    validateContentType("json") mustBe true
    validateContentType("xml") mustBe true
    validateContentType("png") mustBe false
  }

  "validation succeed if regex is matched" in new HeadersValidationHandler {
    matchHeader("application/vnd.hmrc.1.0+json").isDefined mustBe true
    matchHeader("application/vnd.hmrc+json").isDefined mustBe false
  }

  "validation if rules succeed" in new HeadersValidationHandler {
    acceptHeaderValidationRules(Some("application/vnd.hmrc.1.0+json")) mustBe true
  }

  "validate rules" in new HeadersValidationHandler {
    validateRules(Map("Accept" -> "")) mustBe Some(Json.parse("""{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""))
    validateRules(Map("Accept" -> "application/vnd.hmrc.1.0+json")) mustBe None
  }
}
