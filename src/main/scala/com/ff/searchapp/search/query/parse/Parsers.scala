package com.ff.searchapp.search.query.parse

import atto.Atto._
import atto._
import com.ff.searchapp.search.query.Filter.{OptionalField, TextFilter}
import com.ff.searchapp.search.query.Operator.{EQUALS, LIKE}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Operator, SearchTarget}

object Parsers {

  val searchTargetParser: Parser[SearchTarget] =
    string("from:users").map(_ => UserSearch) | string("from:tickets").map(_ => TicketSearch)

  val equalsParser: Parser[Char] = char('=')
  val likeParser: Parser[Char] = char('%')

  val operatorParser: Parser[Operator] = equalsParser.map(_ => EQUALS) | likeParser.map(_ => LIKE)

  val wordParser: Parser[String] = many(letterOrDigit).map(_.mkString)

  def textFieldParser(fieldNameParser: Parser[String]): Parser[TextFilter] = for {
    _ <- skipWhitespace
    fieldName <- fieldNameParser
    _ <- equalsParser
    operator <- operatorParser
    filterValue <- wordParser
    _ <- opt(operatorParser)
  } yield TextFilter(fieldName, operator, filterValue)

  def nullValueOptionalFieldParser(fieldNameParser: Parser[String]): Parser[OptionalField] = for {
    _ <- skipWhitespace
    fieldName <- fieldNameParser
    _ <- equalsParser
    operator <- operatorParser
    filterValue <- opt(wordParser).map {
      case Some("null") => None
      case other => other
    }
    _ <- opt(operatorParser)
  } yield OptionalField(fieldName, operator, filterValue)

  val commaSeparatedValueParser: Parser[String] = many(noneOf(",[]")).map(_.mkString) <~ opt(whitespace) <~ opt(char(',')) <~ opt(whitespace)
}
