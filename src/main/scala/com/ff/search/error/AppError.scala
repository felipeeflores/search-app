package com.ff.search.error

import scala.util.control.NoStackTrace

sealed trait AppError extends NoStackTrace with Product with Serializable

object AppError {
  final case class InvalidRecord(rawJson: String) extends AppError
}
