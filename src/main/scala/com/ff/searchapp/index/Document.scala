package com.ff.searchapp.index

import com.ff.searchapp.model.{IncidentType, Ticket, User}

sealed trait Document[+A] extends Product with Serializable {
  val id: DocumentId
  val data: A
}

object Document {

  final case class UserDocument(id: DocumentId, name: String, data: User) extends Document[User]
  final case class TicketDocument(
    id: DocumentId,
    incidentType: IncidentType,
    subject: String,
    assignee: Option[Int],
    tags: String,
    data: Ticket
  ) extends Document[Ticket]
}
