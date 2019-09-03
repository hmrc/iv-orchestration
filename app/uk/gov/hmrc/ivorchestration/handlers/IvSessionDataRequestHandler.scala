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

package uk.gov.hmrc.ivorchestration.handlers

import java.util.UUID

import cats.{Monad, MonadError}
import cats.syntax.functor._
import cats.syntax.flatMap._
import org.joda.time.DateTime
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.model.api.IvSessionData
import uk.gov.hmrc.ivorchestration.model.core.{IvSessionDataCore, JourneyId}
import uk.gov.hmrc.ivorchestration.repository.IvSessionDataRepositoryAlgebra

class IvSessionDataRequestHandler[F[_]: Monad](authRetrievalAlgebra: IvSessionDataRepositoryAlgebra[F]) {

  def handle(authRetrieval: IvSessionData, headers: Map[String, String])(implicit hc: HeaderCarrier, me: MonadError[F, Throwable]): F[String] =
    generateIdAndPersist(authRetrieval).map(core => buildUri(core.journeyId, headers)) flatMap {
      case None => me.raiseError(new Exception("missing header"))
      case Some(r) => me.pure(r)
    }

  protected def generateIdAndPersist(authRetrieval: IvSessionData)(implicit hc: HeaderCarrier): F[IvSessionDataCore] =
    persist(IvSessionDataCore(authRetrieval, JourneyId(UUID.randomUUID().toString), new DateTime))

  protected def persist(authRetrievalCore: IvSessionDataCore)(implicit hc: HeaderCarrier): F[IvSessionDataCore] =
    authRetrievalAlgebra.insertIvSessionData(authRetrievalCore)

  protected def buildUri(journeyId: JourneyId, headers: Map[String, String]): Option[String] =
    headers.get("Raw-Request-URI").map(location => s"$location/${journeyId.value}" )
}

