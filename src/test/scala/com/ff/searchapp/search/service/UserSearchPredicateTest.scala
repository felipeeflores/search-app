package com.ff.searchapp.search.service

import com.ff.searchapp.model.IncidentType.Task
import com.ff.searchapp.model.{User, UserId, Username}
import com.ff.searchapp.search.query.Filter.{BooleanFilter, IncidentTypeFilter, IntFilter, TextFilter}
import com.ff.searchapp.search.query.SearchField.TicketSearchFields.IncidentTypeField
import com.ff.searchapp.search.query.SearchField.UserSearchFields.{UserIdField, UsernameField, VerifiedField}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Operator, Query}
import org.specs2.mutable.Specification

import java.time.OffsetDateTime

class UserSearchPredicateTest extends Specification {

  "UserSearchPredicate" should {
    val query = Query(
      searchType = UserSearch,
      filters = Vector.empty
    )

    val user = User(
      id = UserId(5),
      name = Username("root"),
      createdAt = OffsetDateTime.parse(s"2021-06-26T19:45:49.0+10:00"),
      verified = false
    )

    "return true for matching user" in {
      val matchingQuery = query.copy(
        filters = Vector(
          IntFilter(UserIdField, Operator.EQUALS, 5),
          TextFilter(UsernameField, Operator.EQUALS, "root"),
          BooleanFilter(VerifiedField, Operator.EQUALS, value = false)
        )
      )

      UserSearchPredicate(matchingQuery)(user) must beTrue
    }

    "return false for non matching user" in {
      val nonMatchingQuery = query.copy(
        filters = Vector(
          BooleanFilter(VerifiedField, Operator.EQUALS, value = true)
        )
      )

      UserSearchPredicate(nonMatchingQuery)(user) must beFalse
    }

    "return true for empty filters" in {
      UserSearchPredicate(
        query = query.copy(
          filters = Vector.empty
        )
      )(
        user = user
      ) must beTrue
    }

    "return true for unknown filters" in {
      UserSearchPredicate(
        query = query.copy(
          filters = Vector(
            IncidentTypeFilter(IncidentTypeField, operator = Operator.EQUALS, value = Task)
          )
        )
      )(
        user = user
      ) must beTrue
    }

    "return true for non user searches" in {
      UserSearchPredicate(
        query = query.copy(
          searchType = TicketSearch,
          filters = Vector(
            BooleanFilter(VerifiedField, Operator.EQUALS, value = false)
          )
        )
      )(
        user = user
      ) must beTrue
    }
  }
}
