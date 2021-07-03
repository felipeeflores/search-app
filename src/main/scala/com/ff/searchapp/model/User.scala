package com.ff.searchapp.model

import cats.Show
import cats.syntax.show._
import io.circe.Decoder

import java.time.OffsetDateTime

final case class User(id: UserId, name: Username, createdAt: OffsetDateTime, verified: Boolean)

object User {

  implicit val userDecoder: Decoder[User] = c =>
    for {
      id <- c.get[UserId]("_id")
      createdAt <- c.get[OffsetDateTime]("created_at")
      username <- c.get[Username]("name")
      verified <- c.get[Option[Boolean]]("verified")
    } yield User(id, username, createdAt, verified.getOrElse(false))

  implicit val userShow: Show[User] = usr => s"""
                                                |\tid: ${usr.id.value.show}
                                                |\tname: ${usr.name.value.show}
                                                |\tcreatedAt: ${usr.createdAt.toString}
                                                |\tverified: ${usr.verified.show}
                                                |""".stripMargin
}
