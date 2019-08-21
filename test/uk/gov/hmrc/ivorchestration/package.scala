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
import uk.gov.hmrc.auth.core.retrieve.{GGCredId, ItmpAddress}
import uk.gov.hmrc.ivorchestration.model.AuthRetrieval

package object ivorchestration {

  val itmpAddress = ItmpAddress(Some("5 Street"),Some("Worthing"),Some("West Sussex"),None,None,Some("BN13 3AS"),Some("England"),Some("44"))
  val authRetrieval = AuthRetrieval(None, GGCredId("777"), Some("123455"),200,
    Some(DateTime.now),Some("123"),Some(itmpAddress),Some("BN13 3AS"),Some("Matt"),Some("Groom"), Some(LocalDate.now), 60)

}
