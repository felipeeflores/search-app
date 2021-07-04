package com.ff.searchapp.search.service

import com.ff.searchapp.TestFixture.sampleOffsetDateTime
import com.ff.searchapp.model.IncidentType.{Incident, Other, Problem, Question, Task}
import com.ff.searchapp.model.{Subject, Ticket, TicketId, UserId}
import com.ff.searchapp.search.query.Filter.{BooleanFilter, IncidentTypeFilter, OptionalIntFilter, TextFilter}
import com.ff.searchapp.search.query.SearchField.TicketSearchFields.{
  AssigneeField, IncidentTypeField, SubjectField, TicketIdField
}
import com.ff.searchapp.search.query.SearchField.UserSearchFields.VerifiedField
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Operator, Query}
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragment

class TicketSearchPredicateTest extends Specification {

  "TicketSearchPredicate" should {
    val query = Query(
      searchType = TicketSearch,
      filters = Vector.empty
    )
    val ticket = Ticket(
      id = TicketId("id-123"),
      createdAt = sampleOffsetDateTime,
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

    "return true for matching ticket id" in {
      val matchingTicket = ticket.copy(id = TicketId("123-abc"))

      "exact match" in {
        val idQuery = query.copy(
          filters = Vector(
            TextFilter(TicketIdField, Operator.EQUALS, "123-abc")
          )
        )
        TicketSearchPredicate(idQuery)(matchingTicket) must beTrue
      }
      "ignoring case" in {
        val idQuery = query.copy(
          filters = Vector(
            TextFilter(TicketIdField, Operator.EQUALS, "123-AbC")
          )
        )
        TicketSearchPredicate(idQuery)(matchingTicket) must beTrue
      }
    }

    "return true for matching subject" in {
      val matchingTicket = ticket.copy(subject = Subject("A Good Client"))

      "with EQUALS operator" in {
        "exact match" in {
          val idQuery = query.copy(
            filters = Vector(
              TextFilter(SubjectField, Operator.EQUALS, "A Good Client")
            )
          )
          TicketSearchPredicate(idQuery)(matchingTicket) must beTrue
        }

        "ignoring case" in {
          val idQuery = query.copy(
            filters = Vector(
              TextFilter(SubjectField, Operator.EQUALS, "a good CLIENT")
            )
          )
          TicketSearchPredicate(idQuery)(matchingTicket) must beTrue
        }
      }

      "with LIKE operator" in {
        "exact match" in {
          val idQuery = query.copy(
            filters = Vector(
              TextFilter(SubjectField, Operator.LIKE, "Good")
            )
          )
          TicketSearchPredicate(idQuery)(matchingTicket) must beTrue
        }

        "ignoring case" in {
          val idQuery = query.copy(
            filters = Vector(
              TextFilter(SubjectField, Operator.LIKE, "GOOD")
            )
          )
          TicketSearchPredicate(idQuery)(matchingTicket) must beTrue
        }
      }
    }

    "return true for matching incident types" in {
      val incidentTypes =
        Vector(
          Incident,
          Problem,
          Question,
          Task,
          Other
        )
      Fragment.foreach(incidentTypes) { incidentType =>
        s"$incidentType" in {
          val incidentTypeQuery = query.copy(
            filters = Vector(
              IncidentTypeFilter(IncidentTypeField, Operator.EQUALS, incidentType)
            )
          )
          val matchingTicket = ticket.copy(incidentType = incidentType)
          TicketSearchPredicate(incidentTypeQuery)(matchingTicket) must beTrue
        }
      }
    }

    "return true for unassigned tickets query" in {
      val unassignedTicketsQuery = query.copy(
        filters = Vector(
          OptionalIntFilter(AssigneeField, Operator.EQUALS, None)
        )
      )
      TicketSearchPredicate(unassignedTicketsQuery)(ticket) must beTrue
    }

    "return false for unassigned tickets when assigned" in {
      val unassignedTicketsQuery = query.copy(
        filters = Vector(
          OptionalIntFilter(AssigneeField, Operator.EQUALS, None)
        )
      )
      val assignedTicket = ticket.copy(assignee = Some(UserId(2)))
      TicketSearchPredicate(unassignedTicketsQuery)(assignedTicket) must beFalse
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

    "return true for non ticket searches, i.e. do not influence results" in {
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
