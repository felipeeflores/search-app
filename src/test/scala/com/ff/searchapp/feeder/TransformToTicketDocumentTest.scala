package com.ff.searchapp.feeder

import com.ff.searchapp.TestFixture.sampleOffsetDateTime
import com.ff.searchapp.index.Document.TicketDocument
import com.ff.searchapp.index.DocumentId
import com.ff.searchapp.model._
import org.specs2.mutable.Specification

class TransformToTicketDocumentTest extends Specification {

  "TransformToTicketDocumentTest" should {
    "transform ticket to ticket document" in {
      val aTicket = Ticket(
        id = TicketId("123-456"),
        createdAt = sampleOffsetDateTime,
        incidentType = IncidentType.Task,
        subject = Subject("Urgent, please fix."),
        assignee = Some(UserId(7)),
        tags = Vector(Tag("eTech"), Tag("Mail"))
      )

      TransformToTicketDocument(aTicket) must beEqualTo(
        TicketDocument(
          id = DocumentId("123-456"),
          data = aTicket
        )
      )
    }
  }
}
