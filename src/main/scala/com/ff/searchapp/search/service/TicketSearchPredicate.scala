package com.ff.searchapp.search.service

import com.ff.searchapp.model.Ticket
import com.ff.searchapp.search.query.Filter._
import com.ff.searchapp.search.query.SearchField.TicketSearchFields.{
  AssigneeField, IncidentTypeField, SubjectField, TicketIdField
}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Filter, Operator, Query}

object TicketSearchPredicate {

  def apply(query: Query)(ticket: Ticket): Boolean = {
    query.searchType match {
      case TicketSearch => filtersToPredicate(ticket, query.filters)
      case UserSearch => true
    }
  }

  private def filtersToPredicate(ticket: Ticket, filters: Vector[Filter]): Boolean = {
    filters.forall {
      case TextFilter(TicketIdField, Operator.EQUALS, value) => ticket.id.value == value
      case TextFilter(SubjectField, Operator.EQUALS, value) => ticket.subject.value == value
      case TextFilter(SubjectField, Operator.LIKE, value) => ticket.subject.value.contains(value)
      case OptionalIntFilter(AssigneeField, Operator.EQUALS, Some(value)) => ticket.assignee.forall(_.value == value)
      case IncidentTypeFilter(IncidentTypeField, Operator.EQUALS, incidentType) => ticket.incidentType == incidentType
      case _ => true
    }
  }
}
