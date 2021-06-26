package com.ff.search.feeder

import com.ff.search.index.Document.TicketDocument
import com.ff.search.index.DocumentId
import com.ff.search.model.Ticket

object TransformToTicketDocument {
  def apply(ticket: Ticket): TicketDocument = {
    TicketDocument(
      id = DocumentId(ticket.id.value),
      incidentType = ticket.incidentType,
      subject = ticket.subject.value,
      assignee = ticket.assignee.map(_.value),
      tags = ticket.tags.map(_.value).mkString(","),
      data = ticket
    )
  }
}
