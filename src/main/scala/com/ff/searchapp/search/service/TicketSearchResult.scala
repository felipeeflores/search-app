package com.ff.searchapp.search.service

import com.ff.searchapp.model.{Ticket, User}

final case class TicketSearchResult(ticket: Ticket, user: Option[User])
