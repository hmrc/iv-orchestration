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

package uk.gov.hmrc.ivorchestration.testsuite

import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.ivorchestration.model.UpliftJourneyType
import uk.gov.hmrc.ivorchestration.model.api.{IvSessionData, IvSessionDataSearchRequest, IvSessionDataSearchResponse}
import uk.gov.hmrc.ivorchestration.model.core.{CredId, IvSessionDataCore, JourneyId}

import java.time.{Instant, LocalDate, OffsetDateTime, ZoneOffset, ZonedDateTime}

trait TestData {
  val anyAffinityGroup: AffinityGroup = Individual

  val sampleIvSessionData: IvSessionData = IvSessionData(Some(CredId("777")), Some("123455"), 200,
    Some(ZonedDateTime.now(ZoneOffset.UTC)), Some("123"), Some("AA12 3BB"),
    Some("Jim"), Some("Smith"), Some(LocalDate.now), Some(anyAffinityGroup), Some("User failed IV"),
    Some(1), UpliftJourneyType
  )

  val currentDateTime: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)

  val currentDateTimeInMillis: Long = currentDateTime.toInstant.toEpochMilli

  val truncatedCurrentDateTime: OffsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(currentDateTimeInMillis), ZoneOffset.UTC)

  val sampleIvSessionDataCore: IvSessionDataCore = IvSessionDataCore(sampleIvSessionData, JourneyId("123"), currentDateTime)

  val buildIvSessionDataCore: IvSessionData => IvSessionDataCore =
    retrieval => IvSessionDataCore(retrieval, JourneyId("123"), OffsetDateTime.now(ZoneOffset.UTC))

  val sampleSearchSessionDataRequest: IvSessionDataSearchRequest = IvSessionDataSearchRequest(JourneyId("123"), Some(CredId("456")))

  val sampleSearchSessionDataResponse: IvSessionDataSearchResponse = IvSessionDataSearchResponse.fromIvSessionDataCore(sampleIvSessionDataCore)
}
