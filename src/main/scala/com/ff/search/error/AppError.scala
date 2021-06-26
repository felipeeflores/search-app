package com.ff.search.error

import cats.Show
import io.circe.Json

import scala.util.control.NoStackTrace

sealed trait AppError extends NoStackTrace with Product with Serializable {
  override def toString = AppError.appErrorShow.show(this)
}

object AppError {
  final case class InvalidRecord(rawJson: Json, errorMessage: String) extends AppError

  implicit val appErrorShow: Show[AppError] = { case InvalidRecord(rawJson, errorMessage) =>
    s"""InvalidRecord(rawJson: ${rawJson.noSpaces}, errorMessage: $errorMessage )"""
  }
}
