package com.ff.search.feeder

import cats.effect.IO

class FeedProcess(
  feedUserIndex: String => IO[Unit],
  feedTicketIndex: String => IO[Unit]
) {
  def run(config: Config): IO[Unit] = {
    for {
      _ <- IO(println("Loading user data"))
      _ <- feedUserIndex(config.usersFile)
      _ <- IO(println("Now loading ticket data"))
      _ <- feedTicketIndex(config.ticketsFile)
      _ <- IO(println("Data loaded successfully...\n"))
    } yield ()
  }
}
