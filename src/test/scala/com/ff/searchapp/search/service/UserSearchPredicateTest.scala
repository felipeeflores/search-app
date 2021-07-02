package com.ff.searchapp.search.service

import com.ff.searchapp.model.IncidentType.Task
import com.ff.searchapp.model.{IncidentType, User, UserId, Username}
import com.ff.searchapp.search.query.Filter.{BooleanFilter, IntFilter, SumTypeFilter, TextFilter}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Operator, Query}
import org.specs2.mutable.Specification

import java.time.OffsetDateTime
import scala.util.Random

class UserSearchPredicateTest extends Specification {

  "UserSearchPredicate" should {
    val query = Query(
      searchType = UserSearch,
      filters = Vector.empty
    )

    val user = User(
      id = UserId(Random.nextInt()),
      name = Username("root"),
      createdAt = OffsetDateTime.parse(s"2021-06-26T19:45:49.0+10:00"),
      verified = false
    )

    "return true for matching user" in {
      val matchingUser = user.copy(
        id = UserId(5),
        verified = true
      )

      val userQuery = query.copy(
        filters = Vector(
          IntFilter("id", Operator.EQUALS, 5),
          TextFilter("username", Operator.EQUALS, "root"),
          BooleanFilter("verified", Operator.EQUALS, value = true)
        )
      )

      UserSearchPredicate(matchingUser, userQuery) must beTrue
    }

    "return false for non matching user" in {
      val nonMatchingUser = user.copy(
        verified = false
      )

      val userQuery = query.copy(
        filters = Vector(
          BooleanFilter("verified", Operator.EQUALS, value = true)
        )
      )

      UserSearchPredicate(nonMatchingUser, userQuery) must beFalse
    }

    "return true for empty filters" in {
      UserSearchPredicate(
        user = user,
        query = query.copy(
          filters = Vector.empty
        )
      ) must beTrue
    }

    "return true for unknown filters" in {
      UserSearchPredicate(
        user = user,
        query = query.copy(
          filters = Vector(
            SumTypeFilter[IncidentType](fieldName = "type", operator = Operator.EQUALS, value = Task)
          )
        )
      ) must beTrue
    }

    "return true for non user searches" in {
      UserSearchPredicate(
        user = user,
        query = query.copy(
          searchType = TicketSearch,
          filters = Vector(
            BooleanFilter("verified", Operator.EQUALS, value = false)
          )
        )
      ) must beTrue
    }
  }
}
