package com.ff.searchapp.search.query.parse

import atto.Atto._
import com.ff.searchapp.model.IncidentType._
import com.ff.searchapp.search.query.Filter.{IncidentTypeFilter, MultipleTextFilter, OptionalIntFilter, TextFilter}
import com.ff.searchapp.search.query.Operator
import com.ff.searchapp.search.query.SearchField.TicketSearchFields._
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragment

class TicketParsersTest extends Specification {

  "TicketParsers" should {
    "parse user filters" in {
      "for ticket search" in {
        "id" in {
          val rawFilter = "id==436bf9b0-1147-4c0a-8439-6f79833bff5b"
          val expectedFilter =
            Vector(TextFilter(TicketIdField, Operator.EQUALS, "436bf9b0-1147-4c0a-8439-6f79833bff5b"))

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
                Vector(IncidentTypeFilter(IncidentTypeField, Operator.EQUALS, expectedIncidentType))

              val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
              result must beRight(expectedFilter)
            }
          }
        }

        "subject" in {
          "EQUALS (=) operator" in {
            val rawFilter = "subject==bar"
            val expectedFilter = Vector(TextFilter(SubjectField, Operator.EQUALS, "bar"))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }

          "LIKE (%) operator" in {
            val rawFilter = s"subject=%Nuisance%"
            val expectedFilter = Vector(TextFilter(SubjectField, Operator.LIKE, "nuisance"))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }
        }

        "assignee" in {
          "with an assignee value" in {
            val rawFilter = "assignee==777"
            val expectedFilter = Vector(OptionalIntFilter(AssigneeField, Operator.EQUALS, Some(777)))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }

          "with an assignee of null" in {
            val rawFilter = "assignee==null"
            val expectedFilter = Vector(OptionalIntFilter(AssigneeField, Operator.EQUALS, None))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }

          "unassigned" in {
            val rawFilter = "unassigned"
            val expectedFilter = Vector(OptionalIntFilter(AssigneeField, Operator.EQUALS, None))

            val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
            result must beRight(expectedFilter)
          }
        }

        "tags" in {
          val rawFilter = "tags=[foo, bar, baz]"
          val expectedFilter = Vector(
            MultipleTextFilter(
              searchField = TagsField,
              operator = Operator.IN,
              values = Vector("foo", "bar", "baz")
            )
          )

          val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
          result must beRight(expectedFilter)
        }.pendingUntilFixed("not supported yet")

        "with all features" in {
          val rawFilter = "id==436bf9b0-1147-4c0a-8439-6f79833bff5b type==task subject=%testing% unassigned"
          val expectedFilter = Vector(
            TextFilter(TicketIdField, Operator.EQUALS, "436bf9b0-1147-4c0a-8439-6f79833bff5b"),
            IncidentTypeFilter(IncidentTypeField, Operator.EQUALS, Task),
            TextFilter(SubjectField, Operator.LIKE, "testing"),
            OptionalIntFilter(AssigneeField, Operator.EQUALS, None)
          )

          val result = TicketParsers.ticketQueryFiltersParser.parseOnly(rawFilter).either
          result must beRight(expectedFilter)
        }
      }
    }
  }
}
