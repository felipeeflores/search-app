package com.ff.searchapp.search.query.parse

import atto.Atto._
import atto._
import cats.Alternative
import com.ff.searchapp.model.IncidentType
import com.ff.searchapp.model.IncidentType.{Incident, Other, Problem, Question, Task}
import com.ff.searchapp.search.query.Filter.{IncidentTypeFilter, MultipleTextFilter, OptionalIntFilter}
import com.ff.searchapp.search.query.Operator.EQUALS
import com.ff.searchapp.search.query.SearchField.TicketSearchFields._
import com.ff.searchapp.search.query.parse.Parsers._
import com.ff.searchapp.search.query.{Filter, Operator}

object TicketParsers {

  private val unassignedParser = for {
    _ <- skipWhitespace
    _ <- string("unassigned")
  } yield OptionalIntFilter(
    searchField = AssigneeField,
    operator = EQUALS,
    value = None
  )

  private val incidentTypeParser: Parser[IncidentType] =
    string("incident").map(_ => Incident) |
      string("problem").map(_ => Problem) |
      string("question").map(_ => Question) |
      string("task").map(_ => Task) |
      Alternative[Parser].pure(Other)

  private val incidentTypeFilterParser: Parser[IncidentTypeFilter] = for {
    _ <- skipWhitespace
    _ <- string(IncidentTypeField.fieldName)
    _ <- equalsParser
    operator <- operatorParser
    incidentType <- incidentTypeParser
  } yield IncidentTypeFilter(IncidentTypeField, operator, incidentType)

  private val assigneeParser: Parser[OptionalIntFilter] =
    nullValueOptionalIntFieldParser(AssigneeField) | unassignedParser

  //TODO: complete this parser
  val tagsParser: Parser[MultipleTextFilter] = for {
    _ <- skipWhitespace
    _ <- string(TagsField.fieldName)
    _ <- equalsParser
    _ <- char('[')
    values <- many(commaSeparatedValueParser)
    _ <- char(']')
  } yield MultipleTextFilter(TagsField, Operator.IN, values.toVector)

  private val oneTicketQueryFilterParser: Parser[Filter] =
    textFieldParser(TicketIdField) | textFieldParser(SubjectField) | incidentTypeFilterParser | assigneeParser

  val ticketQueryFiltersParser: Parser[Vector[Filter]] = many(oneTicketQueryFilterParser).map(_.toVector)
}
