package com.ff.searchapp.search.query.parse

import atto.Atto._
import atto._
import cats.Alternative
import com.ff.searchapp.model.IncidentType
import com.ff.searchapp.model.IncidentType.{Incident, Other, Problem, Question, Task}
import com.ff.searchapp.search.query.Filter.{IncidentTypeField, MultipleTextField, OptionalIntField}
import com.ff.searchapp.search.query.{Filter, Operator}
import com.ff.searchapp.search.query.Operator.EQUALS
import com.ff.searchapp.search.query.parse.Parsers._

object TicketParsers {

  private val ticketTextFieldNameParser: Parser[String] = string("id") | string("subject")

  private val assigneeFieldNameParser = string("assignee")

  private val tagsFieldNameParser = string("tags")

  private val unassignedParser = for {
    _ <- skipWhitespace
    _ <- string("unassigned")
  } yield OptionalIntField(
    fieldName = "assignee",
    operator = EQUALS,
    value = None
  )

  private val incidentTypeParser: Parser[IncidentType] =
    string("incident").map(_ => Incident) |
      string("problem").map(_ => Problem) |
      string("question").map(_ => Question) |
      string("task").map(_ => Task) |
      Alternative[Parser].pure(Other)

  private val incidentTypeFilterParser: Parser[IncidentTypeField] = for {
    _ <- skipWhitespace
    _ <- string("type")
    _ <- equalsParser
    operator <- operatorParser
    incidentType <- incidentTypeParser
  } yield IncidentTypeField("incidentType", operator, incidentType)

  private val assigneeParser: Parser[OptionalIntField] =
    nullValueOptionalIntFieldParser(assigneeFieldNameParser) | unassignedParser

  //TODO: complete this parser
  val tagsParser: Parser[MultipleTextField] = for {
    _ <- skipWhitespace
    fieldName <- tagsFieldNameParser
    _ <- equalsParser
    _ <- char('[')
    values <- many(commaSeparatedValueParser)
    _ <- char(']')
  } yield MultipleTextField(fieldName, Operator.IN, values.toVector)

  private val oneTicketQueryFilterParser: Parser[Filter] =
    textFieldParser(ticketTextFieldNameParser) | incidentTypeFilterParser | assigneeParser

  val ticketQueryFiltersParser: Parser[Vector[Filter]] = many(oneTicketQueryFilterParser).map(_.toVector)
}
