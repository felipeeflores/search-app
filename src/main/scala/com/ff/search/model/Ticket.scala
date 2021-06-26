package com.ff.search.model

import java.time.OffsetDateTime

final case class Ticket(
  id: TicketId,
  createdAt: OffsetDateTime,
  incidentType: IncidentType,
  subject: Subject,
  assigneeId: UserId,
  tags: Vector[Tag]
)
