package com.ff.searchapp.search.service

import com.ff.searchapp.model.{Ticket, User}

final case class UserSearchResult(user: User, tickets: Vector[Ticket])

