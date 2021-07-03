package com.ff.searchapp.search

import cats.Show
import cats.syntax.show._
import com.ff.searchapp.model.{Ticket, User}

sealed trait SearchResult extends Product with Serializable

object SearchResult {
  final case class UserSearchResult(user: User, tickets: Vector[Ticket]) extends SearchResult
  final case class TicketSearchResult(ticket: Ticket, user: Option[User]) extends SearchResult

  implicit val showSearchResult: Show[SearchResult] = {
    case usr @ UserSearchResult(_, _) => usr.show
    case tsr @ TicketSearchResult(_, _) => tsr.show
  }

  implicit val showUserSearchResult: Show[UserSearchResult] = result => s"""
                                                                           |User: ${result.user.show}
                                                                           |  Tickets:
                                                                           |  ${result.tickets.show}
                                                                           |
                                                                           |""".stripMargin

  implicit val showTicketSearchResult: Show[TicketSearchResult] = result => s"""
                                                                               |Ticket: ${result.ticket.show}
                                                                               |  Assignee: ${result.user.show}
                                                                               |
                                                                               |""".stripMargin
}
