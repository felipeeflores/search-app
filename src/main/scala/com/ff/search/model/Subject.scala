package com.ff.search.model

import io.circe.Decoder

final case class Subject(value: String) extends AnyVal

object Subject {
  implicit val subjectDecoder: Decoder[Subject] = Decoder.decodeString.map(Subject(_))
}
