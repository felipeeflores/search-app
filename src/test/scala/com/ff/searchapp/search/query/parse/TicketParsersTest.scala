package com.ff.searchapp.search.query.parse

import atto.Atto._
import com.ff.searchapp.model.IncidentType
import com.ff.searchapp.model.IncidentType._
import com.ff.searchapp.search.query.Filter.{MultipleTextField, OptionalField, SumTypeFilter, TextFilter}
import com.ff.searchapp.search.query.Operator
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragment

class TicketParsersTest extends Specification {

  "TicketParsers" should {
    "parse user filters" in {
      "for ticket search" in {
        "id" in {
          val rawFilter = "id==foo"
          val expectedFilter = Vector(TextFilter("id", Operator.EQUALS, "foo"))

          val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either

          result must beRight(expectedFilter)
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
              val rawFilter = s"type==$rawIncidentType"
              val expectedFilter =
                Vector(SumTypeFilter[IncidentType]("incidentType", Operator.EQUALS, expectedIncidentType))

              val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
              result must beRight(expectedFilter)
            }
          }
        }

        "subject" in {
          "EQUALS (=) operator" in {
            val rawFilter = "subject==bar"
            val expectedFilter = Vector(TextFilter("subject", Operator.EQUALS, "bar"))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }

          "LIKE (%) operator" in {
            val rawFilter = s"subject=%bar%"
            val expectedFilter = Vector(TextFilter("subject", Operator.LIKE, "bar"))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }
        }

        "assignee" in {
          "with an assignee value" in {
            val rawFilter = "assignee==baz"
            val expectedFilter = Vector(OptionalField("assignee", Operator.EQUALS, Some("baz")))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }

          "with an assignee of null" in {
            val rawFilter = "assignee==null"
            val expectedFilter = Vector(OptionalField("assignee", Operator.EQUALS, None))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }

          "unassigned" in {
            val rawFilter = "unassigned"
            val expectedFilter = Vector(OptionalField("assignee", Operator.EQUALS, None))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }
        }

        "tags" in {
          val rawFilter = "tags=[foo, bar, baz]"
          val expectedFilter = Vector(
            MultipleTextField(
              fieldName = "tags",
              operator = Operator.IN,
              values = Vector("foo", "bar", "baz")
            )
          )

          val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
          result must beRight(expectedFilter)
        }.pendingUntilFixed("not supported yet")

        "with all features" in {
          val rawFilter = s"id==foo type==task subject=%testing% unassigned"
          val expectedFilter = Vector(
            TextFilter("id", Operator.EQUALS, "foo"),
            SumTypeFilter[IncidentType]("incidentType", Operator.EQUALS, Task),
            TextFilter("subject", Operator.LIKE, "testing"),
            OptionalField("assignee", Operator.EQUALS, None)
          )

          val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
          result must beRight(expectedFilter)
        }
      }
    }
  }
}
