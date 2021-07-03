package com.ff.searchapp.model

import cats.Show
import io.circe.Decoder

final case class UserId(value: Int) extends AnyVal

object UserId {
  implicit val userIdDecoder: Decoder[UserId] = Decoder.decodeInt.map(UserId(_))
  implicit val userIdShow: Show[UserId] = _.value.toString
}
