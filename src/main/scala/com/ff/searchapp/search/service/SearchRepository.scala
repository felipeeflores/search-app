package com.ff.searchapp.search.service

import cats.Functor
import cats.effect.Ref
import cats.syntax.all._
import com.ff.searchapp.index.{TicketIndex, UserIndex}
import com.ff.searchapp.model.{Ticket, User, UserId}
import com.ff.searchapp.search.query.Query

class SearchRepository[F[_]: Functor](userIndex: Ref[F, UserIndex], ticketIndex: Ref[F, TicketIndex]) {

  val findUsers: Query => F[Vector[User]] = qry => {
    userIndex.get.map(idx =>
      idx.documents.collect {
        case (_, document)  if UserSearchPredicate(qry)(document.data) => document.data
      }.toVector
    )
  }

  val findTicketsForUser: UserId => F[Vector[Ticket]] = userId => {
    ticketIndex.get.map(idx =>
      idx.documents.collect {
        case (_, document)  if document.assignee.forall(_ == userId.value) => document.data
      }.toVector
    )
  }
}
