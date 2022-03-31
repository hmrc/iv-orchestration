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

package uk.gov.hmrc.ivorchestration.testsuite

import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.ivorchestration.model.UpliftJourneyType
import uk.gov.hmrc.ivorchestration.model.api.{IvSessionData, IvSessionDataSearchRequest, IvSessionDataSearchResponse}
import uk.gov.hmrc.ivorchestration.model.core.{CredId, IvSessionDataCore, JourneyId}

import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneOffset}

trait TestData {
  val anyAffinityGroup: AffinityGroup = Individual

  val sampleIvSessionData: IvSessionData = IvSessionData(Some(CredId("777")), Some("123455"), 200,
    Some(LocalTime.now), Some("123"), Some("AA12 3BB"),
    Some("Jim"), Some("Smith"), Some(LocalDate.now), Some(anyAffinityGroup), Some("User failed IV"),
    Some(1), UpliftJourneyType
  )

  val sampleIvSessionDataCore = IvSessionDataCore(sampleIvSessionData, JourneyId("123"), LocalDateTime.now())

  val buildIvSessionDataCore: IvSessionData => IvSessionDataCore =
    retrieval => IvSessionDataCore(retrieval, JourneyId("123"), LocalDateTime.now(ZoneOffset.UTC))

  val sampleSearchSessionDataRequest = IvSessionDataSearchRequest(JourneyId("123"), Some(CredId("456")))

  val sampleSearchSessionDataResponse = IvSessionDataSearchResponse.fromIvSessionDataCore(sampleIvSessionDataCore)
}
