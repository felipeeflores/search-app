package com.ff.searchapp.search.service

import com.ff.searchapp.TestFixture.sampleOffsetDateTime
import com.ff.searchapp.model.IncidentType.Task
import com.ff.searchapp.model.{User, UserId, Username}
import com.ff.searchapp.search.query.Filter.{BooleanFilter, IncidentTypeFilter, IntFilter, TextFilter}
import com.ff.searchapp.search.query.SearchField.TicketSearchFields.IncidentTypeField
import com.ff.searchapp.search.query.SearchField.UserSearchFields.{UserIdField, UsernameField, VerifiedField}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Operator, Query}
import org.specs2.mutable.Specification

class UserSearchPredicateTest extends Specification {

  "UserSearchPredicate" should {
    val query = Query(
      searchType = UserSearch,
      filters = Vector.empty
    )

    val user = User(
      id = UserId(5),
      name = Username("root"),
      createdAt = sampleOffsetDateTime,
      verified = false
    )

    "return true for matching query" in {
      val matchingQuery = query.copy(
        filters = Vector(
          IntFilter(UserIdField, Operator.EQUALS, 5),
          TextFilter(UsernameField, Operator.EQUALS, "root"),
          BooleanFilter(VerifiedField, Operator.EQUALS, value = false)
        )
      )

      UserSearchPredicate(matchingQuery)(user) must beTrue
    }

    "return false for non matching query" in {
      val nonMatchingQuery = query.copy(
        filters = Vector(
          BooleanFilter(VerifiedField, Operator.EQUALS, value = true)
        )
      )

      UserSearchPredicate(nonMatchingQuery)(user) must beFalse
    }

    "return true for matching user id" in {
      val userIdQuery = query.copy(
        filters = Vector(
          IntFilter(UserIdField, Operator.EQUALS, 7)
        )
      )
      val matchingUser = user.copy(id = UserId(7))
      UserSearchPredicate(userIdQuery)(matchingUser) must beTrue
    }

    "return true for matching user name" in {
      "exact match" in {
        val usernameQuery = query.copy(
          filters = Vector(
            TextFilter(UsernameField, Operator.EQUALS, "admin")
          )
        )
        val matchingUser = user.copy(name = Username("admin"))
        UserSearchPredicate(usernameQuery)(matchingUser) must beTrue
      }

      "ignoring case" in {
        val usernameQuery = query.copy(
          filters = Vector(
            TextFilter(UsernameField, Operator.EQUALS, "ADMIN")
          )
        )
        val matchingUser = user.copy(name = Username("Admin"))
        UserSearchPredicate(usernameQuery)(matchingUser) must beTrue
      }

    }

    "return true for verified user" in {
      val verifiedUsernameQuery = query.copy(
        filters = Vector(
          BooleanFilter(VerifiedField, Operator.EQUALS, value = true)
        )
      )
      val matchingUser = user.copy(verified = true)
      UserSearchPredicate(verifiedUsernameQuery)(matchingUser) must beTrue
    }

    "return true for un-verified user" in {
      val unVerifiedUsernameQuery = query.copy(
        filters = Vector(
          BooleanFilter(VerifiedField, Operator.EQUALS, value = false)
        )
      )
      val matchingUser = user.copy(verified = false)
      UserSearchPredicate(unVerifiedUsernameQuery)(matchingUser) must beTrue
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

    "return true for non user searches, i.e. do not influence results" in {
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
