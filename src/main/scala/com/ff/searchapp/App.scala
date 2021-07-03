package com.ff.searchapp

import cats.effect.kernel.Ref
import cats.effect.std.Console
import cats.effect.{ExitCode, IO, IOApp}
import com.ff.searchapp.index.Document.{TicketDocument, UserDocument}
import com.ff.searchapp.index.{Index, TicketIndex, UserIndex}

import scala.collection.mutable
import scala.concurrent.duration.DurationInt

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    Console[IO].println("Welcome to search-app") >> start

  def start: IO[ExitCode] = {
    for {
      userIndexRef <- Ref.of[IO, UserIndex](Index[UserDocument](mutable.HashMap.empty))
      ticketIndexRef <- Ref.of[IO, TicketIndex](Index[TicketDocument](mutable.HashMap.empty))
      config <- IO(Config.loadConfig)
      _ <- Console[IO].println("\nRunning with config:")
      _ <- Console[IO].println(config)
      di = DI(userIndexRef, ticketIndexRef, config.maxSearchResults)
      _ <- di.feedProcess.run(config)
      totalUsers <- userIndexRef.get.map(_.documents.size)
      totalTickets <- ticketIndexRef.get.map(_.documents.size)
      _ <- Console[IO].println(s"Total users loaded: ${totalUsers.toString}")
      _ <- Console[IO].println(s"Total tickets loaded: ${totalTickets.toString}")
      _ <- Console[IO].println("\nLoading basic search interface...\n")
      _ <- IO.sleep(500.millis)
      _ <- di.searchClient.run
    } yield ExitCode.Success
  }
}
