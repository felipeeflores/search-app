package com.ff.search.feeder

import com.ff.search.index.Document.TicketDocument
import com.ff.search.index.DocumentId
import com.ff.search.model._
import org.specs2.mutable.Specification

import java.time.OffsetDateTime

class TransformToTicketDocumentTest extends Specification {

  "TransformToTicketDocumentTest" should {
    "transform ticket to ticket document" in {
      val aTicket = Ticket(
        id = TicketId("123-456"),
        createdAt = OffsetDateTime.parse(s"2021-06-26T19:45:49.0+10:00"),
        incidentType = IncidentType.Task,
        subject = Subject("Urgent, please fix."),
        assignee = Some(UserId(7)),
        tags = Vector(Tag("eTech"), Tag("Mail"))
      )

      TransformToTicketDocument(aTicket) must beEqualTo(
        TicketDocument(
          id = DocumentId("123-456"),
          incidentType = IncidentType.Task,
          subject = "Urgent, please fix.",
          assignee = Some(7),
          tags = "eTech,Mail",
          data = aTicket
        )
      )
    }
  }
}
