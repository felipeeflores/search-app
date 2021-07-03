package com.ff.searchapp.search.query

sealed trait SearchField extends Product with Serializable {
  val fieldName: String
}

object SearchField {
  object UserSearchFields {
    case object UserIdField extends SearchField { val fieldName = "id" }
    case object UsernameField extends SearchField { val fieldName = "username" }
    case object VerifiedField extends SearchField { val fieldName = "verified" }
  }

  object TicketSearchFields {
    case object TicketIdField extends SearchField { val fieldName = "id" }
    case object IncidentTypeField extends SearchField { val fieldName = "type" }
    case object SubjectField extends SearchField { val fieldName = "subject" }
    case object AssigneeField extends SearchField { val fieldName = "assignee" }
    case object TagsField extends SearchField { val fieldName = "tags" }
  }
}

