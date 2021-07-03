package com.ff.searchapp.search.service

import cats.effect.IO
import com.ff.searchapp.TestFixture.sampleOffsetDateTime
import com.ff.searchapp.model.IncidentType.Task
import com.ff.searchapp.model.{Subject, Ticket, TicketId, User, UserId, Username}
import com.ff.searchapp.search.query.Query
import com.ff.searchapp.search.query.SearchTarget.UserSearch
import org.specs2.matcher.IOMatchers
import org.specs2.mutable.Specification

class SearchServiceTest extends Specification with IOMatchers {

  "SearchService" should {
    val anyQuery = Query(UserSearch, Vector.empty)

    "search users with their tickets" in {
      val expectedResult = Vector(
        UserSearchResult(testUser, Vector(testTicket, anotherTicket)),
        UserSearchResult(anotherUser, Vector.empty)
      )
      searchService.searchUsers(anyQuery) must returnValue(expectedResult)
    }

    "search tickets with its assigned user if available" in {
      val expectedResult = Vector(
        TicketSearchResult(testTicket, Some(testUser)),
        TicketSearchResult(unassignedTicket, None),
        TicketSearchResult(orphanTicket, None)
      )
      searchService.searchTickets(anyQuery) must returnValue(expectedResult)
    }
  }

  private val testUser = User(
    id = UserId(1),
    name = Username("root"),
    createdAt = sampleOffsetDateTime,
    verified = true
  )
  private val anotherUser = testUser.copy(
    id = UserId(2),
    name = Username("admin")
  )
  private val testTicket = Ticket(
    id = TicketId("123-abc"),
    createdAt = sampleOffsetDateTime,
    incidentType = Task,
    subject = Subject("test subject"),
    assignee = Some(testUser.id),
    tags = Vector.empty
  )
  private val anotherTicket = testTicket.copy(id = TicketId("456-xyz"))
  private val unassignedTicket = testTicket.copy(id = TicketId("789-def"), assignee = None)
  private val orphanTicket = testTicket.copy(id = TicketId("789-def"), assignee = Some(UserId(777)))

  private val searchService = new SearchService[IO](
    findUsers = _ => IO.pure(Vector(testUser, anotherUser)),
    findUserTickets = {
      case UserId(1) => IO.pure(Vector(testTicket, anotherTicket))
      case _ => IO.pure(Vector.empty)
    },
    findTickets = _ => IO.pure(Vector(testTicket, unassignedTicket, orphanTicket)),
    findUserForTicket = {
      case UserId(1) => IO.pure(Some(testUser))
      case _ => IO.pure(None)
    }
  )
}
