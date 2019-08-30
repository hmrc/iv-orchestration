/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc

import org.joda.time.{DateTime, LocalDate}
import uk.gov.hmrc.auth.core.retrieve.ItmpAddress
import uk.gov.hmrc.ivorchestration.model.{AuthRetrieval, AuthRetrievalCore, JourneyId}

package object ivorchestration {

  val sampleItmpAddress = ItmpAddress(Some("5 Street"),Some("Worthing"),Some("West Sussex"),None,None,Some("BN13 3AS"),Some("England"),Some("44"))
  val sampleAuthRetrieval = AuthRetrieval(JourneyId("123"), "777", Some("123455"),200,
    Some(DateTime.now),Some("123"),Some(sampleItmpAddress),Some("BN13 3AS"),Some("Matt"),Some("Groom"), Some(LocalDate.now))

  val sampleAuthRetrievalCore = AuthRetrievalCore(sampleAuthRetrieval, new DateTime)

  val buildRetrievalCore: AuthRetrieval => AuthRetrievalCore = retrieval => AuthRetrievalCore(retrieval, new DateTime)

}
