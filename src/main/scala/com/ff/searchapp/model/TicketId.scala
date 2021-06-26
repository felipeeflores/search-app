package com.ff.searchapp.model

import io.circe.Decoder

final case class TicketId(value: String) extends AnyVal

object TicketId {
  implicit val ticketIdDecoder: Decoder[TicketId] = Decoder.decodeString.map(TicketId(_))
}
