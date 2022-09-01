package caloree.routes

import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{HttpRoutes, QueryParamDecoder}

import cats.effect.kernel.Concurrent

import caloree.model.Types._
import caloree.model._
import caloree.query.DayInstanceQuery.MealWithFoods
import caloree.query.Run

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Routes {
  implicit val localDateQueryParamD: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.localDate(DateTimeFormatter.BASIC_ISO_DATE)

  object CustomFoodIdP extends QueryParamDecoderMatcher[EntityId[CustomFood]]("custom_food_id")
  object DescriptionP  extends QueryParamDecoderMatcher[Description]("description")
  object PageP         extends QueryParamDecoderMatcher[Page]("page")
  object Limit         extends QueryParamDecoderMatcher[Int]("limit")
  object FoodIdP       extends QueryParamDecoderMatcher[EntityId[Food]]("food_id")
  object DateP         extends QueryParamDecoderMatcher[LocalDate]("date")
  object GramsP        extends QueryParamDecoderMatcher[Grams]("grams")

  def routes[F[_]: Concurrent](
      implicit
      auth: AuthMiddleware[F, User],
      r1: Run.Optional[F, (Username, AccessToken), User],
      r2: Run.Optional[F, (EntityId[CustomFood], EntityId[User]), CustomFood],
      r3: Run.Optional[F, (EntityId[Food], Grams), Food],
      r4: Run.Unique[F, (Username, Password), AccessToken],
      r5: Run.Unique[F, (EntityId[User], LocalDate, List[MealWithFoods]), Int],
      r6: Run.Many[F, (Description, EntityId[User]), CustomFoodPreview],
      r7: Run.Many[F, Description, FoodPreview],
      r8: Run.Many[F, (EntityId[User], LocalDate), MealFood]
  ): HttpRoutes[F] = Router(
    "auth"        -> AuthRoutes.routes,
    "meal-food"   -> auth(MealFoodRoutes.routes),
    "custom-food" -> auth(CustomFoodRoutes.routes),
    "food"        -> auth(FoodRoutes.routes)
  )
}
