package com.ff.searchapp.model

import cats.Show
import io.circe.Decoder

final case class TicketId(value: String) extends AnyVal

object TicketId {
  implicit val ticketIdDecoder: Decoder[TicketId] = Decoder.decodeString.map(TicketId(_))
  implicit val ticketShow: Show[TicketId] = _.value
}
