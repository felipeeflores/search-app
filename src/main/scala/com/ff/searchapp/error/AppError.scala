package com.ff.searchapp.error

import cats.Show
import io.circe.Json

import scala.util.control.NoStackTrace

sealed trait AppError extends NoStackTrace with Product with Serializable {
  override def toString = AppError.appErrorShow.show(this)
}

object AppError {
  final case class InvalidRecord(rawJson: Json, errorMessage: String) extends AppError
  final case class UnexpectedError(step: String, throwable: Throwable) extends AppError
  final case class InvalidSearchQuery(rawQuery: String, errorHint: String) extends AppError

  implicit val appErrorShow: Show[AppError] = {
    case InvalidRecord(rawJson, errorMessage) => s"""InvalidRecord(rawJson: ${rawJson.noSpaces}, errorMessage: $errorMessage )"""
    case UnexpectedError(step, throwable) => s"UnexpectedError(step: $step, throwable: ${throwable.getMessage})"
    case InvalidSearchQuery(rawQuery, errorHint) => s"InvalidSearchType(rawQuery: '$rawQuery', errorHint: '$errorHint')"
  }
}
