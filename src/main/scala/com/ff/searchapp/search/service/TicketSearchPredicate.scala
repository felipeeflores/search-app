package com.ff.searchapp.search.service

import com.ff.searchapp.model.Ticket
import com.ff.searchapp.search.query.Filter._
import com.ff.searchapp.search.query.SearchField.TicketSearchFields.{
  AssigneeField, IncidentTypeField, SubjectField, TicketIdField
}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Filter, Operator, Query}
import com.ff.searchapp.{compareStringContainsAnother, compareStringEquals}

object TicketSearchPredicate {

  def apply(query: Query)(ticket: Ticket): Boolean = {
    query.searchType match {
      case TicketSearch => filtersToPredicate(ticket, query.filters)
      case UserSearch => true
    }
  }

  private def filtersToPredicate(ticket: Ticket, filters: Vector[Filter]): Boolean = {
    filters.forall {
      case TextFilter(TicketIdField, Operator.EQUALS, value) => compareStringEquals(ticket.id.value, value)
      case TextFilter(SubjectField, Operator.EQUALS, value) => compareStringEquals(ticket.subject.value, value)
      case TextFilter(SubjectField, Operator.LIKE, value) => compareStringContainsAnother(ticket.subject.value, value)
      case OptionalIntFilter(AssigneeField, Operator.EQUALS, Some(value)) => ticket.assignee.forall(_.value == value)
      case OptionalIntFilter(AssigneeField, Operator.EQUALS, None) => ticket.assignee.isEmpty
      case IncidentTypeFilter(IncidentTypeField, Operator.EQUALS, incidentType) => ticket.incidentType == incidentType
      case _ => true
    }
  }
}
