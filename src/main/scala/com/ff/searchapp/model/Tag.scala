package com.ff.searchapp.model

import cats.Show
import io.circe.Decoder

final case class Tag(value: String) extends AnyVal

object Tag {
  implicit val tagDecoder: Decoder[Tag] = Decoder.decodeString.map(Tag(_))
  implicit val tagShow: Show[Tag] = _.value
}
