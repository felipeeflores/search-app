package com.ff.searchapp.model

import cats.Show
import cats.syntax.show._
import com.ff.searchapp.{optionShow, vectorShow}
import io.circe.Decoder

import java.time.OffsetDateTime

final case class Ticket(
  id: TicketId,
  createdAt: OffsetDateTime,
  incidentType: IncidentType,
  subject: Subject,
  assignee: Option[UserId],
  tags: Vector[Tag]
)

object Ticket {

  implicit val ticketDecoder: Decoder[Ticket] = c =>
    for {
      id <- c.get[TicketId]("_id")
      createdAt <- c.get[OffsetDateTime]("created_at")
      incidentType <- c.get[Option[IncidentType]]("type")
      subject <- c.get[Subject]("subject")
      assignee <- c.get[Option[UserId]]("assignee_id")
      tags <- c.get[Vector[Tag]]("tags")
    } yield Ticket(id, createdAt, incidentType.getOrElse(IncidentType.Other), subject, assignee, tags)

  implicit val userOptionShow: Show[Option[UserId]] = optionShow[UserId]
  implicit val vectorTicketShow: Show[Vector[Tag]] = vectorShow[Tag]

  implicit val ticketShow: Show[Ticket] = ticket => s"""
                                                       |\tid: ${ticket.id.value.show}
                                                       |\tcreatedAt: ${ticket.createdAt.toString}
                                                       |\ttype: ${ticket.incidentType.show}
                                                       |\tsubject: ${ticket.subject.value.show}
                                                       |\tassignee id: ${ticket.assignee.show}
                                                       |\ttags: ${ticket.tags.show}
                                                       |""".stripMargin
}
