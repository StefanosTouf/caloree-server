package caloree.query

import doobie._
import doobie.implicits._

import caloree.model.CustomFoodPreview
import caloree.model.Types.{Description, Limit, Page, UID}

object CustomFoodPreviewQuery {
  def customFoodsPreviewByDescription(
      description: Description,
      user: UID,
      page: Page,
      limit: Limit)(
      implicit lh: LogHandler
  ): ConnectionIO[List[CustomFoodPreview]] =
    if (description.string.isEmpty) {
      sql"""
        select id, user_id, description
        from   custom_food
        where  user_id = $user
        limit  $limit
        offset $page * $limit
      """.query[CustomFoodPreview].to
    } else {
      sql"""
        select id, user_id, description
        from   custom_food
        where  description_tsvector @@ plainto_tsquery('english', $description)
        and    user_id = $user
        limit  $limit
        offset $page * $limit
      """.query[CustomFoodPreview].to
    }

}
