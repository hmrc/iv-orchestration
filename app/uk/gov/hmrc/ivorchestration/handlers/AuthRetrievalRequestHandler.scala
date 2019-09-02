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

import cats.{ Monad, MonadError}
import cats.syntax.functor._
import cats.syntax.flatMap._
import org.joda.time.DateTime
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ivorchestration.model.{AuthRetrieval, AuthRetrievalCore}
import uk.gov.hmrc.ivorchestration.services.AuthRetrievalAlgebra

class AuthRetrievalRequestHandler[F[_]: Monad](authRetrievalAlgebra: AuthRetrievalAlgebra[F]) {

  def handle(authRetrieval: AuthRetrieval, headers: Map[String, String])(implicit hc: HeaderCarrier, me: MonadError[F, Throwable]): F[String] =
    generateIdAndPersist(authRetrieval).map(core => buildUri(core.authRetrieval.journeyId, headers)) flatMap {
      case None => me.raiseError(new Exception("missing header"))
      case Some(r) => me.pure(r)
    }

  protected def generateIdAndPersist(authRetrieval: AuthRetrieval)(implicit hc: HeaderCarrier): F[AuthRetrievalCore] =
    persist(AuthRetrievalCore(authRetrieval.copy(journeyId = Some(UUID.randomUUID().toString)), new DateTime))

  protected def persist(authRetrievalCore: AuthRetrievalCore)(implicit hc: HeaderCarrier): F[AuthRetrievalCore] =
    authRetrievalAlgebra.insertAuthRetrieval(authRetrievalCore)

  protected def buildUri(journeyId: Option[String], headers: Map[String, String]): Option[String] =
   for {
    id <- journeyId
    uri <- headers.get("Raw-Request-URI")
  } yield s"$uri/$id"
}

