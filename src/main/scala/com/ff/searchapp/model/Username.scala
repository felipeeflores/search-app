package com.ff.searchapp.model

import io.circe.Decoder

final case class Username(value: String) extends AnyVal

object Username {
  implicit val usernameDecoder: Decoder[Username] = Decoder.decodeString.map(Username(_))
}
