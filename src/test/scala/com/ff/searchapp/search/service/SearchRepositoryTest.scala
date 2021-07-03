package com.ff.searchapp.search.service

import cats.effect.{IO, Ref}
import com.ff.searchapp.TestFixture.sampleOffsetDateTime
import com.ff.searchapp.index.Document.{TicketDocument, UserDocument}
import com.ff.searchapp.index.{DocumentId, Index, TicketIndex, UserIndex}
import com.ff.searchapp.model.IncidentType.Task
import com.ff.searchapp.model._
import com.ff.searchapp.search.query.Filter.TextFilter
import com.ff.searchapp.search.query.SearchField.TicketSearchFields.TicketIdField
import com.ff.searchapp.search.query.SearchField.UserSearchFields.UsernameField
import com.ff.searchapp.search.query.SearchTarget.{TicketSearch, UserSearch}
import com.ff.searchapp.search.query.{Operator, Query}
import org.specs2.matcher.IOMatchers
import org.specs2.mutable.Specification

import scala.collection.mutable

class SearchRepositoryTest extends Specification with IOMatchers {

  "SearchRepository" should {
    "find users by query" in {
      val query = Query(UserSearch, Vector(TextFilter(UsernameField, Operator.EQUALS, "root")))
      repository.flatMap(_.findUsers(query)) must returnValue(Vector(testUser))
    }

    "find tickets for user" in {
      repository.flatMap(_.findTicketsForUser(testUser.id)) must returnValue(Vector(testTicket))
    }

    "find tickets by query" in {
      val query = Query(TicketSearch, Vector(TextFilter(TicketIdField, Operator.EQUALS, "123-abc")))
      repository.flatMap(_.findTickets(query)) must returnValue(Vector(testTicket))
    }

    "find user for ticket" in {
      repository.flatMap(_.findUserForTicket(testUser.id)) must returnValue(Some(testUser))
    }
  }

  private val testUser = User(
    id = UserId(1),
    name = Username("root"),
    createdAt = sampleOffsetDateTime,
    verified = true
  )
  private val testTicket = Ticket(
    id = TicketId("123-abc"),
    createdAt = sampleOffsetDateTime,
    incidentType = Task,
    subject = Subject("test subject"),
    assignee = Some(testUser.id),
    tags = Vector.empty
  )
  private val repository = for {
    userRef <- Ref.of[IO, UserIndex](Index(documents = mutable.HashMap.empty))
    ticketRef <- Ref.of[IO, TicketIndex](Index(documents = mutable.HashMap.empty))
    _ <- userRef.getAndUpdate(uIdx => {
      uIdx.documents.addOne(
        DocumentId("1") -> UserDocument(DocumentId("1"), data = testUser)
      )
      uIdx
    })
    _ <- ticketRef.getAndUpdate(tIdx => {
      tIdx.documents.addOne(
        DocumentId("123-abc") -> TicketDocument(
          DocumentId("123-abc"),
          testTicket
        )
      )
      tIdx
    })
  } yield new SearchRepository[IO](userRef, ticketRef)
}
