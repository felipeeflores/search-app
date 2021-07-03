package com.ff.searchapp.search.query.parse

import atto.Atto._
import atto._
import cats.Alternative
import com.ff.searchapp.search.query.Filter.{BooleanFilter, IntFilter, OptionalIntFilter, TextFilter}
import com.ff.searchapp.search.query.Operator.{EQUALS, LIKE}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Operator, SearchField, SearchTarget}

object Parsers {

  val searchTargetParser: Parser[SearchTarget] =
    string("from:users").map(_ => UserSearch) | string("from:tickets").map(_ => TicketSearch)

  val equalsParser: Parser[Char] = char('=')
  val likeParser: Parser[Char] = char('%')

  val operatorParser: Parser[Operator] = equalsParser.map(_ => EQUALS) | likeParser.map(_ => LIKE)

  val textDataParser: Parser[Char] = noneOf("[]" + "%" + "=" + "," + " " + '\r'.toString + '\n'.toString)
  val wordParser: Parser[String] = many(textDataParser).map(_.mkString)

  def textFieldParser(searchField: SearchField): Parser[TextFilter] = for {
    _ <- skipWhitespace
    _ <- string(searchField.fieldName)
    _ <- equalsParser
    operator <- operatorParser
    filterValue <- wordParser
    _ <- opt(operatorParser)
  } yield TextFilter(searchField, operator, filterValue)

  def nullValueOptionalIntFieldParser(searchField: SearchField): Parser[OptionalIntFilter] = for {
    _ <- skipWhitespace
    _ <- string(searchField.fieldName)
    _ <- equalsParser
    operator <- operatorParser
    filterValue <- opt(int) | Alternative[Parser].pure(None)
    _ <- opt(operatorParser)
  } yield OptionalIntFilter(searchField, operator, filterValue)

  val commaSeparatedValueParser: Parser[String] =
    many(noneOf(",[]")).map(_.mkString) <~ opt(whitespace) <~ opt(char(',')) <~ opt(whitespace)

  private val booleanParser = string("true").map(_ => true) | string("false").map(_ => false)

  def intFieldParser(searchField: SearchField): Parser[IntFilter] = for {
    _ <- skipWhitespace
    _ <- string(searchField.fieldName)
    _ <- equalsParser
    operator <- operatorParser
    value <- int
    _ <- opt(operatorParser)
  } yield IntFilter(searchField, operator, value)

  def booleanFieldParser(searchField: SearchField): Parser[BooleanFilter] = for {
    _ <- skipWhitespace
    _ <- string(searchField.fieldName)
    _ <- equalsParser
    operator <- operatorParser
    booleanValue <- booleanParser
    _ <- opt(operatorParser)
  } yield BooleanFilter(searchField, operator, booleanValue)
}
