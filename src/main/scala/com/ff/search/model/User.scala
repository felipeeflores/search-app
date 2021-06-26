package com.ff.search.model

import io.circe.Decoder

import java.time.OffsetDateTime

final case class User(id: UserId, name: Username, createdAt: OffsetDateTime, verified: Boolean)

object User {

  implicit val userDecoder: Decoder[User] = c => for {
    id <- c.get[UserId]("_id")
    createdAt <- c.get[OffsetDateTime]("created_at")
    username <- c.get[Username]("name")
    verified <- c.get[Boolean]("verified")
  } yield User(id, username, createdAt, verified)
}
