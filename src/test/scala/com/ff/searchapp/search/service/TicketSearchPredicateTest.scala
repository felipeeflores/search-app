package com.ff.searchapp.search.service

import com.ff.searchapp.model.IncidentType.{Problem, Question}
import com.ff.searchapp.model.{Subject, Ticket, TicketId}
import com.ff.searchapp.search.query.Filter.{BooleanFilter, IncidentTypeField, OptionalIntField, TextFilter}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Operator, Query}
import org.specs2.mutable.Specification

import java.time.OffsetDateTime

class TicketSearchPredicateTest extends Specification {

  "TicketSearchPredicate" should {
    val query = Query(
      searchType = TicketSearch,
      filters = Vector.empty
    )
    val ticket = Ticket(
      id = TicketId("id-123"),
      createdAt = OffsetDateTime.parse(s"2021-06-26T19:45:49.0+10:00"),
      incidentType = Problem,
      subject = Subject("testing"),
      assignee = None,
      tags = Vector.empty
    )

    "return true for matching query" in {
      val matchingQuery = query.copy(
        filters = Vector(
          TextFilter("id", Operator.EQUALS, "id-123"),
          TextFilter("subject", Operator.LIKE, "test"),
          OptionalIntField("assignee", Operator.EQUALS, None),
          IncidentTypeField("type", Operator.EQUALS, Problem)
        )
      )
      TicketSearchPredicate(ticket, matchingQuery) must beTrue
    }

    "return false for non-matching query" in {
      val nonMatchingQuery = query.copy(
        filters = Vector(
          IncidentTypeField("type", Operator.EQUALS, Question)
        )
      )
      TicketSearchPredicate(ticket, nonMatchingQuery) must beFalse
    }

    "return true for empty filters" in {
      TicketSearchPredicate(
        ticket = ticket,
        query = query.copy(
          filters = Vector.empty
        )
      ) must beTrue
    }

    "return true for unknown filters" in {
      TicketSearchPredicate(
        ticket = ticket,
        query = query.copy(
          filters = Vector(
            BooleanFilter("verified", Operator.EQUALS, value = true)
          )
        )
      ) must beTrue
    }

    "return true for non ticket searches" in {
      TicketSearchPredicate(
        ticket = ticket,
        query = query.copy(
          searchType = UserSearch,
          filters = Vector(
            IncidentTypeField("type", Operator.EQUALS, Problem)
          )
        )
      ) must beTrue
    }
  }
}
