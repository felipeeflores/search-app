package com.ff.searchapp.feeder

import com.ff.searchapp.index.Document.TicketDocument
import com.ff.searchapp.index.DocumentId
import com.ff.searchapp.model.Ticket

object TransformToTicketDocument {
  def apply(ticket: Ticket): TicketDocument = {
    TicketDocument(
      id = DocumentId(ticket.id.value),
      data = ticket
    )
  }
}
