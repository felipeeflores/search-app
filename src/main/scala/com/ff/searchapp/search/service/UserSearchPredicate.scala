package com.ff.searchapp.search.service

import com.ff.searchapp.model.User
import com.ff.searchapp.search.query.Filter._
import com.ff.searchapp.search.query.SearchField.UserSearchFields.{UserIdField, UsernameField, VerifiedField}
import com.ff.searchapp.search.query.{Filter, Operator, Query}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}

object UserSearchPredicate {

  def apply(query: Query)(user: User): Boolean = {
    query.searchType match {
      case UserSearch => filtersToPredicate(user, query.filters)
      case TicketSearch => true
    }
  }

  private def filtersToPredicate(user: User, filters: Vector[Filter]): Boolean = {
    filters.forall {
      case IntFilter(UserIdField, Operator.EQUALS, value) => user.id.value == value
      case TextFilter(UsernameField, Operator.EQUALS, value) => user.name.value == value
      case BooleanFilter(VerifiedField, Operator.EQUALS, value) => user.verified == value
      case _ => true
    }
  }
}
