package com.ff.searchapp.model

import io.circe.Decoder

final case class UserId(value: Int) extends AnyVal

object UserId {
  implicit val userIdDecoder: Decoder[UserId] = Decoder.decodeInt.map(UserId(_))
}
