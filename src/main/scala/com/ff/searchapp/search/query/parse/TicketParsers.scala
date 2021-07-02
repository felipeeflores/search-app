package com.ff.searchapp.search.query.parse

import atto.Atto._
import atto._
import cats.Alternative
import com.ff.searchapp.model.IncidentType
import com.ff.searchapp.model.IncidentType.{Incident, Other, Problem, Question, Task}
import com.ff.searchapp.search.query.Filter.{MultipleTextField, OptionalField, SumTypeFilter}
import com.ff.searchapp.search.query.Operator
import com.ff.searchapp.search.query.Operator.EQUALS
import com.ff.searchapp.search.query.parse.Parsers._

object TicketParsers {

  val ticketTextFieldNameParser: Parser[String] = string("id") | string("subject")

  private val assigneeFieldNameParser = string("assignee")

  private val tagsFieldNameParser = string("tags")

  private val unassignedParser = for {
    _ <- skipWhitespace
    _ <- string("unassigned")
  } yield OptionalField(
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

  val incidentTypeFilterParser: Parser[SumTypeFilter[IncidentType]] = for {
    _ <- skipWhitespace
    _ <- string("type")
    _ <- equalsParser
    operator <- operatorParser
    incidentType <- incidentTypeParser
  } yield SumTypeFilter("incidentType", operator, incidentType)

  val assigneeParser: Parser[OptionalField] = nullValueOptionalFieldParser(assigneeFieldNameParser) | unassignedParser

  val tagsParser: Parser[MultipleTextField] = for {
    _ <- skipWhitespace
    fieldName <- tagsFieldNameParser
    _ <- equalsParser
    _ <- char('[')
    values <- many(commaSeparatedValueParser)
    _ <- char(']')
  } yield MultipleTextField(fieldName, Operator.IN, values.toVector)
}
