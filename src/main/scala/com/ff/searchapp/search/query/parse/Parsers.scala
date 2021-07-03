package com.ff.searchapp.search.query.parse

import atto.Atto._
import atto._
import cats.Alternative
import com.ff.searchapp.search.query.Filter.{BooleanFilter, IntFilter, OptionalIntFilter, TextFilter}
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

  def nullValueOptionalIntFieldParser(fieldNameParser: Parser[String]): Parser[OptionalIntFilter] = for {
    _ <- skipWhitespace
    fieldName <- fieldNameParser
    _ <- equalsParser
    operator <- operatorParser
    filterValue <- opt(int) | Alternative[Parser].pure(None)
    _ <- opt(operatorParser)
  } yield OptionalIntFilter(fieldName, operator, filterValue)

  val commaSeparatedValueParser: Parser[String] =
    many(noneOf(",[]")).map(_.mkString) <~ opt(whitespace) <~ opt(char(',')) <~ opt(whitespace)

  private val booleanParser = string("true").map(_ => true) | string("false").map(_ => false)

  def intFieldParser(fieldNameParser: Parser[String]): Parser[IntFilter] = for {
    _ <- skipWhitespace
    fieldName <- fieldNameParser
    _ <- equalsParser
    operator <- operatorParser
    value <- int
    _ <- opt(operatorParser)
  } yield IntFilter(fieldName, operator, value)

  def booleanFieldParser(fieldNameParser: Parser[String]): Parser[BooleanFilter] = for {
    _ <- skipWhitespace
    fieldName <- fieldNameParser
    _ <- equalsParser
    operator <- operatorParser
    booleanValue <- booleanParser
    _ <- opt(operatorParser)
  } yield BooleanFilter(fieldName, operator, booleanValue)
}
