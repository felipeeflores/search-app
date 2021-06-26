package com.ff.search.model

import io.circe.Decoder

import java.time.OffsetDateTime

final case class Ticket(
  id: TicketId,
  createdAt: OffsetDateTime,
  incidentType: IncidentType,
  subject: Subject,
  assignee: UserId,
  tags: Vector[Tag]
)

object Ticket {

  implicit val ticketDecoder: Decoder[Ticket] = c =>
    for {
      id <- c.get[TicketId]("_id")
      createdAt <- c.get[OffsetDateTime]("created_at")
      incidentType <- c.get[IncidentType]("type")
      subject <- c.get[Subject]("subject")
      assignee <- c.get[UserId]("assignee_id")
      tags <- c.get[Vector[Tag]]("tags")
    } yield Ticket(id, createdAt, incidentType, subject, assignee, tags)
}
