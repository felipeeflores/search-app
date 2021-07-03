package com.ff.searchapp.search.client

import cats.effect.std.Console
import cats.effect.{IO, Sync}
import cats.syntax.all._
import com.ff.searchapp.error.AppError.{InvalidSearchQuery, UnexpectedError}
import com.ff.searchapp.error.ErrorOr
import com.ff.searchapp.search.SearchResult
import com.ff.searchapp.search.query.Query
import com.ff.searchapp.search.query.parse.ParseQuery

class SearchClient[F[_]: Sync: Console](
  parseQuery: String => ErrorOr[Query],
  search: Query => F[Vector[SearchResult]]
) {

  def run: F[Unit] = {
    for {
      _ <- Console[F].println("Welcome to search...\n")
      _ <- printUsage
      _ <- loop
    } yield ()
  }

  private def printUsage: F[Unit] = {
    for {
      _ <- Console[F].println("Usage:\n")
      _ <- Console[F].println("User search:")
      _ <- Console[F].println("\tfrom:users <filters>")
      _ <- Console[F].println("\twhere:")
      _ <- Console[F].println("\t\tfilters is one of:")
      _ <- Console[F].println("\t\t\tid==a-user-id username==a-username verified==<true|false>")
      _ <- Console[F].println("Ticket search:")
      _ <- Console[F].println("\tfrom:tickets <filters>")
      _ <- Console[F].println("\twhere:")
      _ <- Console[F].println("\t\tfilters is one of:")
      _ <- Console[F].println(
        "\t\t\tid==a-ticket-id type==<incident,problem,question,task> subject=%testing% unassigned"
      )
      _ <- Console[F].println("\t\t\tsubject=%a-subject% unassigned|assigned==<a-user-id>")
      _ <- Console[F].println("")
    } yield ()
  }
  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  private def loop: F[Unit] = {
    val doSearch = for {
      _ <- Console[F].println("Enter your query (type help for usage, Ctrl+C to exit):")
      rawQuery <- Console[F].readLine
      searchResults <- runSearch(rawQuery)
      _ <- Console[F].println(searchResults)
      _ <- Console[F].println("\n")
    } yield ()

    Sync[F].handleErrorWith(doSearch)(handleError) >> loop
  }

  private def runSearch(rawQuery: String): F[Vector[SearchResult]] = {
    for {
      query <- Sync[F].fromEither(parseQuery(rawQuery))
      searchResults <- search(query)
    } yield searchResults
  }

  private def handleError(throwable: Throwable): F[Unit] = {
    throwable match {
      case InvalidSearchQuery("", _) => Sync[F].unit
      case InvalidSearchQuery("help", _) => printUsage
      case err @ InvalidSearchQuery(_, _) =>
        Console[F].println("Invalid search query. Type help") >> Console[F].errorln(err)
      case err @ UnexpectedError(_, _) =>
        Console[F].println("Unexpected error") >> Console[F].errorln(err)
      case other => Console[F].println("Failure") >> Console[F].errorln(other)
    }
  }
}

object SearchClient {
  def apply(search: Query => IO[Vector[SearchResult]]): SearchClient[IO] = {
    new SearchClient[IO](
      parseQuery = ParseQuery.apply,
      search = search
    )
  }
}
