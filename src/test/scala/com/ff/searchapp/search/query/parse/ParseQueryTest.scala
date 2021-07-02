package com.ff.searchapp.search.query.parse

import cats.syntax.all._
import com.ff.searchapp.model.IncidentType
import com.ff.searchapp.model.IncidentType.{Incident, Other, Problem, Question, Task}
import com.ff.searchapp.search.query.Filter.{MultipleTextField, OptionalField, SumTypeFilter, TextFilter}
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Operator, Query}
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragment

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
          val rawSearch = s"$ticketRawSearch id==foo"
          val expectedQuery = ticketSearchQuery.copy(filters = Vector(TextFilter("id", Operator.EQUALS, "foo")))

          ParseQuery(rawSearch) must beRight(expectedQuery)
        }

        "incident type" in {
          val incidentTypes =
            Vector(
              "incident" -> Incident,
              "problem" -> Problem,
              "question" -> Question,
              "task" -> Task,
              "foo" -> Other
            )
          Fragment.foreach(incidentTypes) { incidentType =>
            val (rawIncidentType, expectedIncidentType) = incidentType

            s"$rawIncidentType" in {
              val rawSearch = s"$ticketRawSearch type==$rawIncidentType"
              val expectedQuery = ticketSearchQuery.copy(
                filters = Vector(
                  SumTypeFilter[IncidentType]("incidentType", Operator.EQUALS, expectedIncidentType)
                )
              )
              ParseQuery(rawSearch) must beRight(expectedQuery)
            }
          }
        }

        "subject" in {
          "EQUALS (=) operator" in {
            val rawSearch = s"$ticketRawSearch subject==bar"
            val expectedQuery = ticketSearchQuery.copy(filters = Vector(TextFilter("subject", Operator.EQUALS, "bar")))

            ParseQuery(rawSearch) must beRight(expectedQuery)
          }

          "LIKE (%) operator" in {
            val rawSearch = s"$ticketRawSearch subject=%bar%"
            val expectedQuery = ticketSearchQuery.copy(filters = Vector(TextFilter("subject", Operator.LIKE, "bar")))

            ParseQuery(rawSearch) must beRight(expectedQuery)
          }
        }

        "assignee" in {
          "with an assignee value" in {
            val rawSearch = s"$ticketRawSearch assignee==baz"
            val expectedQuery =
              ticketSearchQuery.copy(filters = Vector(OptionalField("assignee", Operator.EQUALS, Some("baz"))))

            ParseQuery(rawSearch) must beRight(expectedQuery)
          }

          "with an assignee of null" in {
            val rawSearch = s"$ticketRawSearch assignee==null"
            val expectedQuery =
              ticketSearchQuery.copy(filters = Vector(OptionalField("assignee", Operator.EQUALS, None)))

            ParseQuery(rawSearch) must beRight(expectedQuery)
          }

          "unassigned" in {
            val rawSearch = s"$ticketRawSearch unassigned"
            val expectedQuery =
              ticketSearchQuery.copy(filters = Vector(OptionalField("assignee", Operator.EQUALS, None)))

            ParseQuery(rawSearch) must beRight(expectedQuery)
          }
        }

        "tags" in {
          val rawSearch = s"$ticketRawSearch tags=[foo, bar, baz]"
          val expectedQuery =
            ticketSearchQuery.copy(
              filters = Vector(
                MultipleTextField(
                  fieldName = "tags",
                  operator = Operator.IN,
                  values = Vector("foo", "bar", "baz")
                )
              )
            )

          ParseQuery(rawSearch) must beRight(expectedQuery)
        }.pendingUntilFixed("not supported yet")

        "with all features" in {
          val fullSearch = s"$ticketRawSearch id==foo type==task subject=%testing% unassigned"
          val expectedQuery = ticketSearchQuery.copy(
            filters = Vector(
              TextFilter("id", Operator.EQUALS, "foo"),
              SumTypeFilter[IncidentType]("incidentType", Operator.EQUALS, Task),
              TextFilter("subject", Operator.LIKE, "testing"),
              OptionalField("assignee", Operator.EQUALS, None)
            )
          )

          ParseQuery(fullSearch) must beRight(expectedQuery)
        }
      }
    }
  }
}
