package com.ff.searchapp.search.query.parse

import atto.Atto._
import atto._
import cats.Alternative
import cats.syntax.all._
import com.ff.searchapp.error.AppError.UnexpectedError
import com.ff.searchapp.error.ErrorOr
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.parse.Parsers._
import com.ff.searchapp.search.query.parse.TicketParsers.ticketQueryFiltersParser
import com.ff.searchapp.search.query.parse.UserParsers.userQueryFiltersParser
import com.ff.searchapp.search.query.{Filter, Query, SearchTarget}

import java.util.Locale

object ParseQuery {

  def apply(rawQuery: String): ErrorOr[Query] = {
    val rawQueryLc = rawQuery.toLowerCase(Locale.ENGLISH)

    parseQuery
      .parseOnly(rawQueryLc)
      .either
      .leftMap(_ => UnexpectedError(rawQuery, new RuntimeException("boom")))
  }

  private def parseQuery: Parser[Query] = for {
    searchTarget <- searchTargetParser
    filters <- filtersParser(searchTarget)
  } yield Query(searchTarget, filters)

  private def filtersParser(searchTarget: SearchTarget): Parser[Vector[Filter]] = {
    val filtersParser = searchTarget match {
      case TicketSearch => ticketQueryFiltersParser
      case UserSearch => userQueryFiltersParser
    }
    filtersParser | Alternative[Parser].pure(Vector.empty)
  }
}
