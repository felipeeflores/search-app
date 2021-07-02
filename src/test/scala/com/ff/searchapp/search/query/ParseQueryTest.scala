package com.ff.searchapp.search.query

import org.specs2.mutable.Specification
import cats.syntax.all._
import com.ff.searchapp.search.query.Filter.TextFilter
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}

class ParseQueryTest extends Specification {

  "QueryParser" should {

    "parse search target" in {
      val userQuery = Query(UserSearch, Vector.empty)
      val ticketsQuery = Query(TicketSearch, Vector.empty)

      "as a user search" in {
        ParseQuery("from:users") must beRight(userQuery)
      }

      "as a ticket search" in {
        ParseQuery("from:tickets") must beRight(ticketsQuery)
      }

      "ignoring text case" in {
        val res = Vector("FROM:USERS", "From:Tickets").traverse(ParseQuery(_))
        res must beRight(Vector(userQuery, ticketsQuery))
      }

    }

    "parse filters" in {
      "for ticket search" in {
        val ticketRawSearch = "from:tickets"
        val ticketSearchQuery = Query(TicketSearch, Vector.empty)

        "id" in {
          val rawSearch = s"$ticketRawSearch id=foo"
          val expectedQuery = ticketSearchQuery.copy(filter = Vector(TextFilter("id", Operator.EQUALS, "foo")))

          ParseQuery(rawSearch) must beRight(expectedQuery)
        }
      }
    }
  }
}
