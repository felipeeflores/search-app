package com.ff.searchapp.model

import cats.Show
import io.circe.Decoder

final case class Subject(value: String) extends AnyVal

object Subject {
  implicit val subjectDecoder: Decoder[Subject] = Decoder.decodeString.map(Subject(_))
  implicit val subjectShow: Show[Subject] = _.value
}
