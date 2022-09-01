package caloree.query

import doobie._
import doobie.implicits._

import caloree.model.Types.{Description, EntityId, Page}
import caloree.model.{CustomFood, CustomFoodPreview, User}

object CustomFoodPreviewQuery {
  def customFoodsPreviewByDescription(
      description: Description,
      user: EntityId[User],
      page: Page,
      limit: Int)(
      implicit lh: LogHandler): ConnectionIO[List[CustomFoodPreview]] =
    sql"""
      select id, description
      from   custom_food
      where  description_tsvector @@ to_tsquery('english', $description)
      and    user_id = $user
      limit  $limit
      offset $page * $limit
    """
      .query[CustomFoodPreview]
      .to[List]

}
