package com.ff.searchapp.feeder

import com.ff.searchapp.error.AppError.InvalidRecord
import com.ff.searchapp.error.ErrorOr
import io.circe.{Decoder, Json}
import cats.syntax.show._

object Decode {
  def apply[A: Decoder](json: Json): ErrorOr[A] = {
    Decoder[A]
      .apply(json.hcursor)
      .left
      .map(decodingFailure => InvalidRecord(json, decodingFailure.show))
  }
}
