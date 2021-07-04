package com.ff.searchapp.client

import cats.effect.std.Console
import cats.effect.{IO, Sync}
import cats.syntax.all._
import com.ff.searchapp.error.AppError.{InvalidSearchQuery, UnexpectedError}
import com.ff.searchapp.error.ErrorOr
import com.ff.searchapp.search.SearchResult
import com.ff.searchapp.search.query.Query
import com.ff.searchapp.search.query.parse.ParseQuery
import com.ff.searchapp.vectorShow

class SearchClient[F[_]: Sync: Console](
  parseQuery: String => ErrorOr[Query],
  search: Query => F[Vector[SearchResult]],
  maxSearchResults: Option[Int]
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
      _ <- Console[F].println("\nUsage:\n")
      _ <- Console[F].println(" User search:")
      _ <- Console[F].println("\tfrom:users <filters>")
      _ <- Console[F].println("\twhere:")
      _ <- Console[F].println("\t\tfilters are one or more of:")
      _ <- Console[F].println("\t\t\tid==a-user-id username==a-username verified==<true|false>")
      _ <- Console[F].println(" Ticket search:")
      _ <- Console[F].println("\tfrom:tickets <filters>")
      _ <- Console[F].println("\twhere:")
      _ <- Console[F].println("\t\tfilters are one or more of:")
      _ <- Console[F].println("\t\t\tid==a-ticket-id type==<incident,problem,question,task> subject=%testing%")
      _ <- Console[F].println("\t\t\tsubject=%a-subject% unassigned|assignee==<a-user-id>|assigned==null")
      _ <- Console[F].println("")
      _ <- Console[F].println("\tSample queries:")
      _ <- Console[F].println("\t\t- Unassigned tasks: from:tickets unassigned type==task")
      _ <- Console[F].println("\t\t- Subject like (one word only): from:tickets subject=%nuisance%")
    } yield ()
  }
  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  private def loop: F[Unit] = {
    val doSearch = for {
      _ <- Console[F].println("Enter your query (type help for usage, Ctrl+C to exit):")
      rawQuery <- Console[F].readLine
      searchResults <- runSearch(rawQuery)
      takeResults = maxSearchResults
        .map(_ min searchResults.size)
        .getOrElse(searchResults.size)
      _ <- Console[F].println("\nSearch Results:\n")
      _ <- Console[F].println(searchResults.take(takeResults))(vectorShow)
      _ <- Console[F].println("\n")
      _ <- Console[F].println(s"Total results: ${searchResults.size.toString}")
      _ <- Console[F].println(s"Showing: ${takeResults.toString}")
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
  def apply(search: Query => IO[Vector[SearchResult]], maxSearchResults: Option[Int]): SearchClient[IO] = {
    new SearchClient[IO](
      parseQuery = ParseQuery.apply,
      search = search,
      maxSearchResults = maxSearchResults
    )
  }
}
