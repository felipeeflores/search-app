package com.ff.searchapp.index

import com.ff.searchapp.model.{Ticket, User}

sealed trait Document[+A] extends Product with Serializable {
  val id: DocumentId
  val data: A
}

object Document {

  final case class UserDocument(id: DocumentId, data: User) extends Document[User]
  final case class TicketDocument(
    id: DocumentId,
    data: Ticket
  ) extends Document[Ticket]
}
