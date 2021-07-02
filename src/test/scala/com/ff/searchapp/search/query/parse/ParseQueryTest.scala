package com.ff.searchapp.search.query.parse

import cats.syntax.all._
import com.ff.searchapp.error.AppError.InvalidSearchQuery
import com.ff.searchapp.search.query.{Filter, Query}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import org.specs2.mutable.Specification

class ParseQueryTest extends Specification {

  "QueryParser" should {

    val userQuery = Query(UserSearch, Vector.empty)
    val ticketsQuery = Query(TicketSearch, Vector.empty)

    "parse search target" in {
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

    "parse filters accordingly" in {
      "for user search" in {
        ParseQuery("from:users id==123 verified==true username==foo").map(_.filters.size) must beRight(3)
      }

      "for ticket search" in {
        ParseQuery("from:tickets id==foo type==task subject=%goal% assignee==tom").map(_.filters.size) must beRight(4)
      }

      "ignore invalid filters" in {
        ParseQuery("from:tickets unit==ict").map(_.filters) must beRight(Vector.empty[Filter])
      }
    }

    "handle invalid queries" in {
      val expectedError = InvalidSearchQuery(
        rawQuery = "from:entities id==fail",
        errorHint = """Failure reading:string("from:tickets")"""
      )
      ParseQuery("from:entities id==fail") must beLeft(expectedError)
    }
  }
}
