package com.ff.searchapp.search.query.parse

import atto.Atto._
import atto._
import cats.Alternative
import cats.syntax.all._
import com.ff.searchapp.error.AppError.UnexpectedError
import com.ff.searchapp.error.ErrorOr
import com.ff.searchapp.search.query.Query
import com.ff.searchapp.search.query.parse.Parsers._
import com.ff.searchapp.search.query.parse.TicketParsers._

import java.util.Locale

object ParseQuery {

  def apply(rawQuery: String): ErrorOr[Query] = {
    val rawQueryLc = rawQuery.toLowerCase(Locale.ENGLISH)

    parseQuery
      .parseOnly(rawQueryLc)
      .either
      .leftMap(_ => UnexpectedError(rawQuery, new RuntimeException("boom")))
  }

  private val filterParser =
    textFieldParser(ticketTextFieldNameParser) |
      incidentTypeFilterParser |
      assigneeParser

  private val filtersParser =
    many(filterParser).map(_.toVector) |
      Alternative[Parser].pure(Vector.empty)

  private val parseQuery = for {
    searchTarget <- searchTargetParser
    filters <- filtersParser
  } yield Query(searchTarget, filters)

}
