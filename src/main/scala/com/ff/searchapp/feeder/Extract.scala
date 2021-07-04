package com.ff.searchapp.feeder

import cats.effect.IO
import com.ff.searchapp.error.AppError.ExtractFileError
import fs2.Stream
import fs2.io.file.Files

import java.nio.file.Path
import scala.util.Try

object Extract {

  def apply(rawPath: String): Stream[IO, Byte] = {
    val pathF = IO.fromTry(Try(Path.of(rawPath)))
    Stream
      .eval(pathF)
      .flatMap(path => Files[IO].readAll(path, 4096))
      .handleErrorWith { _ =>
        val appError = ExtractFileError(rawPath)
        Stream.raiseError[IO](appError)
      }
  }
}
