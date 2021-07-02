package com.ff.searchapp.search.query

import com.ff.searchapp.error.AppError.UnexpectedError
import com.ff.searchapp.error.ErrorOr
import atto._
import Atto._
import cats.Alternative
import cats.syntax.all._
import com.ff.searchapp.search.query.Filter.TextFilter
import com.ff.searchapp.search.query.Operator.{EQUALS, IN, LIKE}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}

object ParseQuery {

  def apply(rawQuery: String): ErrorOr[Query] = {
    val rawQueryLc = rawQuery.toLowerCase

    parseQuery
      .parseOnly(rawQueryLc)
      .either
      .leftMap(_ => UnexpectedError(rawQuery, new RuntimeException("boom")))

  }

  private val searchTargetParser: Parser[SearchTarget] =
    string("from:users").map(_ => UserSearch) | string("from:tickets").map(_ => TicketSearch)

  private val operatorParser =
    char('=').map(_ => EQUALS) |
      char('%').map(_ => LIKE) |
      char('~').map(_ => IN)

  private val parseId = string("id")

  private val wordParser = takeWhile(c => !c.isWhitespace)

  private val textFieldParser: Parser[Vector[Filter]] = for {
    _ <- skipWhitespace
   fieldName <- parseId
   operator <- operatorParser
   filterValue <- wordParser
  } yield Vector(TextFilter(fieldName, operator, filterValue))

  private val filterParser = textFieldParser | Alternative[Parser].pure(Vector.empty[Filter])

  private val parseQuery = for {
    searchTarget <- searchTargetParser
    filters <- filterParser
  } yield Query(searchTarget, filters)

}
