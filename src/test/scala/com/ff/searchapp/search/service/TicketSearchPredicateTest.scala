package com.ff.searchapp.search.service

import com.ff.searchapp.model.IncidentType.{Problem, Question}
import com.ff.searchapp.model.{Subject, Ticket, TicketId}
import com.ff.searchapp.search.query.Filter.{BooleanFilter, IncidentTypeFilter, OptionalIntFilter, TextFilter}
import com.ff.searchapp.search.query.SearchField.TicketSearchFields.{
  AssigneeField, IncidentTypeField, SubjectField, TicketIdField
}
import com.ff.searchapp.search.query.SearchField.UserSearchFields.VerifiedField
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
          TextFilter(TicketIdField, Operator.EQUALS, "id-123"),
          TextFilter(SubjectField, Operator.LIKE, "test"),
          OptionalIntFilter(AssigneeField, Operator.EQUALS, None),
          IncidentTypeFilter(IncidentTypeField, Operator.EQUALS, Problem)
        )
      )
      TicketSearchPredicate(matchingQuery)(ticket) must beTrue
    }

    "return false for non-matching query" in {
      val nonMatchingQuery = query.copy(
        filters = Vector(
          IncidentTypeFilter(IncidentTypeField, Operator.EQUALS, Question)
        )
      )
      TicketSearchPredicate(nonMatchingQuery)(ticket) must beFalse
    }

    "return true for empty filters" in {
      TicketSearchPredicate(
        query = query.copy(filters = Vector.empty)
      )(
        ticket = ticket
      ) must beTrue
    }

    "return true for unknown filters" in {
      TicketSearchPredicate(
        query = query.copy(
          filters = Vector(
            BooleanFilter(VerifiedField, Operator.EQUALS, value = true)
          )
        )
      )(
        ticket = ticket
      ) must beTrue
    }

    "return true for non ticket searches" in {
      TicketSearchPredicate(
        query = query.copy(
          searchType = UserSearch,
          filters = Vector(
            IncidentTypeFilter(IncidentTypeField, Operator.EQUALS, Problem)
          )
        )
      )(
        ticket = ticket
      ) must beTrue
    }
  }
}
