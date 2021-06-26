package com.ff.searchapp.feeder

import cats.effect.Sync
import com.ff.searchapp.error.{AppError, ErrorOr}
import fs2.{Pipe, Stream}
import io.circe.Json

class Feeder[F[_]: Sync, A, B](
  extract: String => Stream[F, Byte],
  parse: Pipe[F, Byte, Json],
  decode: Json => ErrorOr[A],
  transform: A => B,
  load: B => F[Unit],
  handleError: AppError => F[Unit]
) {
  def feed(rawPath: String): F[Unit] = {
    extract(rawPath)
      .through(parse)
      .map(decode)
      .evalMap {
        case Right(b) => load(transform(b))
        case Left(error) => handleError(error)
      }
      .compile
      .drain
  }
}
