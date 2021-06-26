package com.ff.search

package object model {

  final case class UserId(value: Int) extends AnyVal
  final case class Username(value: String) extends AnyVal

  final case class TicketId(value: String) extends AnyVal
  final case class Subject(value: String) extends AnyVal
  final case class Tag(value: String) extends AnyVal

  sealed trait IncidentType extends Product with Serializable
  case object Incident extends IncidentType
  case object Problem extends IncidentType
  case object Question extends IncidentType
  case object Task extends IncidentType

}
