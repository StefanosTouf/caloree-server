package caloree.routes

import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

import cats.Monad

import caloree.model.Types.{Description, EntityId, Page}
import caloree.model.{CustomFood, CustomFoodPreview, User}
import caloree.query.Run
import caloree.routes.Routes._
import caloree.util._

object CustomFoodRoutes {
  def routes[F[_]: Monad](
      implicit
      go: Run.Optional[F, (EntityId[CustomFood], EntityId[User]), CustomFood],
      gm: Run.Many[F, (Description, EntityId[User]), CustomFoodPreview]
  ): AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of {
      case GET -> _ :? CustomFoodIdP(id) as User(uid, _, _) =>
        go.run((id, uid)).asResponse

      case GET -> _ :? DescriptionP(desc) +& PageP(page) +& Limit(limit) as User(uid, _, _) =>
        gm.run((desc, uid), page, limit).asResponse
    }
  }
}
