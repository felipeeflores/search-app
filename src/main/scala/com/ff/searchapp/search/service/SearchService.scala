package com.ff.searchapp.search.service

import cats.syntax.all._
import cats.{Monad, Parallel}
import com.ff.searchapp.model.{Ticket, User, UserId}
import com.ff.searchapp.search.SearchResult
import com.ff.searchapp.search.SearchResult.{TicketSearchResult, UserSearchResult}
import com.ff.searchapp.search.query.Query
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}

final class SearchService[F[_]: Monad: Parallel](
  findUsers: Query => F[Vector[User]],
  findUserTickets: UserId => F[Vector[Ticket]],
  findTickets: Query => F[Vector[Ticket]],
  findUserForTicket: UserId => F[Option[User]]
) {

  def search(query: Query): F[Vector[SearchResult]] = {
    query.searchType match {
      case UserSearch => searchUsers(query)
      case TicketSearch => searchTickets(query)
    }
  }

  private def searchUsers(query: Query): F[Vector[SearchResult]] = {
    findUsers(query).flatMap { users =>
      users.parTraverse(user =>
        findUserTickets(user.id)
          .map(UserSearchResult(user, _))
      )
    }
  }

  private def searchTickets(query: Query): F[Vector[SearchResult]] = {
    findTickets(query).flatMap { tickets =>
      tickets.parTraverse(ticket =>
        ticket.assignee
          .flatTraverse(findUserForTicket)
          .map(TicketSearchResult(ticket, _))
      )
    }
  }
}
