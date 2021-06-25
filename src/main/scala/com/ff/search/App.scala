package com.ff.search

import cats.effect.{ExitCode, IO, IOApp}

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    IO(println("Welcome to search-app")).map(_ => ExitCode.Success)
}
