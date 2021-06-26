package com.ff.search.feeder

import com.ff.search.model.IncidentType.Incident
import com.ff.search.model.{Subject, Tag, Ticket, TicketId, User, UserId, Username}
import io.circe.literal.JsonStringContext
import org.specs2.mutable.Specification

import java.time.OffsetDateTime

class DecodeTest extends Specification {

  "Decode" should {
    "decode User" in {
      val userJson =
        json"""
            {
              "_id": 1,
              "name": "Gilberto de Piento",
              "created_at": "2016-04-15T05:19:46-10:00",
              "verified": true
            }
            """
      val expected = User(
        id = UserId(1),
        name = Username("Gilberto de Piento"),
        createdAt = OffsetDateTime.parse("2016-04-15T05:19:46-10:00"),
        verified = true
      )
      Decode[User](userJson) must beRight(expected)
    }

    "decode Ticket" in {
      val ticketJson =
        json"""
            {
              "_id": "436bf9b0-1147-4c0a-8439-6f79833bff5b",
              "created_at": "2016-04-28T11:19:34-10:00",
              "type": "incident",
              "subject": "A very important subject.",
              "assignee_id": 24,
              "tags": [
                "Tag",
                "Another tag"
              ]
            }
            """
      val expected = Ticket(
        id = TicketId("436bf9b0-1147-4c0a-8439-6f79833bff5b"),
        createdAt = OffsetDateTime.parse("2016-04-28T11:19:34-10:00"),
        incidentType = Incident,
        subject = Subject("A very important subject."),
        assignee = UserId(24),
        tags = Vector(
          Tag("Tag"),
          Tag("Another tag")
        )
      )
      Decode[Ticket](ticketJson) must beRight(expected)
    }
  }
}
