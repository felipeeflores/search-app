package com.ff.searchapp.search.service

import cats.syntax.all._
import cats.{Monad, Parallel}
import com.ff.searchapp.model.{Ticket, User, UserId}
import com.ff.searchapp.search.query.Query

final class SearchService[F[_]: Monad: Parallel](
  findUsers: Query => F[Vector[User]],
  findUserTickets: UserId => F[Vector[Ticket]],
  findTickets: Query => F[Vector[Ticket]],
  findUserForTicket: UserId => F[Option[User]]
) {

  def searchUsers(query: Query): F[Vector[UserSearchResult]] = {
    findUsers(query).flatMap { users =>
      users.parTraverse(user =>
        findUserTickets(user.id)
          .map(UserSearchResult(user, _))
      )
    }
  }

  def searchTickets(query: Query): F[Vector[TicketSearchResult]] = {
    findTickets(query).flatMap { tickets =>
      tickets.parTraverse(ticket =>
        ticket.assignee
          .flatTraverse(findUserForTicket)
          .map(TicketSearchResult(ticket, _))
      )
    }
  }
}
