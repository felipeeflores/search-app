package com.ff.searchapp.search.query

sealed trait SearchTarget extends Product with Serializable

object SearchTarget {
  case object UserSearch extends SearchTarget
  case object TicketSearch extends SearchTarget
}
