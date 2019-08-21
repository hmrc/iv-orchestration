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

package uk.gov.hmrc.ivorchestration.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.BaseSpec
import uk.gov.hmrc.ivorchestration.persistence.ReactiveMongoConnector
import uk.gov.hmrc.mongo.{MongoConnector, MongoSpecSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class SessionDataDBServiceSpec extends BaseSpec with MongoSpecSupport {

  implicit val hc = HeaderCarrier()

  val reactiveMongoComponent: ReactiveMongoConnector = new ReactiveMongoConnector {
    override def mongoConnector: MongoConnector = mongoConnectorForTest
  }

  "can Add and retrieve SessionData entity" in {
    val service = new SessionDataDBService(reactiveMongoComponent)

    val eventualData: Future[List[SessionData]] = for {
      _    <- service.createSessionData(SessionData("123"))
      data <- service.findSessionData("123")
    } yield data

    Await.result(eventualData, 20 seconds).head mustBe SessionData("123")
  }
}
