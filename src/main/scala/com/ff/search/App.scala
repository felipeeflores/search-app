package com.ff.search

import cats.effect.kernel.Ref
import cats.effect.{ExitCode, IO, IOApp}
import com.ff.search.feeder.Config
import com.ff.search.index.Document.{TicketDocument, UserDocument}
import com.ff.search.index.{Index, TicketIndex, UserIndex}

import scala.collection.mutable

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    IO(println("Welcome to search-app")) >> start()

  def start(): IO[ExitCode] = {
    for {
      userIndexRef <- Ref.of[IO, UserIndex](Index[UserDocument](mutable.HashMap.empty))
      ticketIndexRef <- Ref.of[IO, TicketIndex](Index[TicketDocument](mutable.HashMap.empty))
      di = DI(userIndexRef, ticketIndexRef)
      _ <- di.feedProcess.run(Config.defaultConfig)
      totalUsers <- userIndexRef.get.map(_.documents.size)
      totalTickets <- ticketIndexRef.get.map(_.documents.size)
      _ <- IO(println(s"Total users loaded: ${totalUsers.toString}"))
      _ <- IO(println(s"Total tickets loaded: ${totalTickets.toString}"))
    } yield ExitCode.Success
  }
}
