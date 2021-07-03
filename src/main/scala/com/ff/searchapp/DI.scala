package com.ff.searchapp

import cats.effect.{IO, Ref}
import cats.syntax.show._
import com.ff.searchapp.error.AppError
import com.ff.searchapp.feeder._
import com.ff.searchapp.index.Document.{TicketDocument, UserDocument}
import com.ff.searchapp.index.{IndexManager, TicketIndex, UserIndex}
import com.ff.searchapp.model.{Ticket, User}
import com.ff.searchapp.search.client.SearchClient
import com.ff.searchapp.search.service.{SearchRepository, SearchService}
import io.circe.fs2.byteArrayParser

/*
 Class to glue together all the individual production components.
 Better DI: Passing and type checking all the arguments/parameters at compile time.
 */
final case class DI(userIndexRef: Ref[IO, UserIndex], ticketIndexRef: Ref[IO, TicketIndex]) {
  private val userIndexManager = new IndexManager[IO, UserDocument, User](userIndexRef)
  private val ticketIndexManager = new IndexManager[IO, TicketDocument, Ticket](ticketIndexRef)

  private def handleError(error: AppError) = IO(println(s"ERROR: ${error.show}"))

  private val userFeeder = new Feeder[IO, User, UserDocument](
    extract = Extract.apply,
    parse = byteArrayParser,
    decode = Decode.apply[User](_),
    transform = TransformToUserDocument.apply,
    load = userIndexManager.addDocument,
    handleError = handleError
  )

  private val ticketFeeder = new Feeder[IO, Ticket, TicketDocument](
    extract = Extract.apply,
    parse = byteArrayParser,
    decode = Decode.apply[Ticket](_),
    transform = TransformToTicketDocument.apply,
    load = ticketIndexManager.addDocument,
    handleError = handleError
  )

  val feedProcess: FeedProcess = new FeedProcess(feedUserIndex = userFeeder.feed, feedTicketIndex = ticketFeeder.feed)

  private val searchRepository = new SearchRepository[IO](userIndex = userIndexRef, ticketIndex = ticketIndexRef)
  private val searchService = new SearchService[IO](
    findUsers = searchRepository.findUsers,
    findUserTickets = searchRepository.findTicketsForUser,
    findTickets = searchRepository.findTickets,
    findUserForTicket = searchRepository.findUserForTicket
  )

  val searchClient: SearchClient[IO] = SearchClient(searchService.search)
}
