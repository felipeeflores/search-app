package com.ff.searchapp.feeder

import cats.effect.IO
import cats.syntax.show._
import com.ff.searchapp.Config
import com.ff.searchapp.error.AppError
import com.ff.searchapp.error.AppError.UnexpectedError

class FeedProcess(
  feedUserIndex: String => IO[Unit],
  feedTicketIndex: String => IO[Unit]
) {
  def run(config: Config): IO[Unit] = {
    val process = for {
      _ <- IO(println("\nLoading user data"))
      _ <- feedUserIndex(config.usersFile)
      _ <- IO(println("Now loading ticket data"))
      _ <- feedTicketIndex(config.ticketsFile)
      _ <- IO(println("\nData loaded successfully...\n"))
    } yield ()

    process.handleErrorWith(throwable => {
      val appError = throwable match {
        case appError: AppError =>
          IO(println(s"Failed feeding with AppError: ${appError.show}.")).map(_ => appError)
        case _ =>
          for {
            _ <- IO(println(s"Failed feeding with unexpected error: ${throwable.getMessage}"))
            _ <- if (config.verboseErrors) IO(throwable.printStackTrace()) else IO.unit
          } yield UnexpectedError("Feeding data", throwable)
      }
      IO(println("\nAborting...")) >> appError.flatMap(IO.raiseError(_))
    })
  }
}
