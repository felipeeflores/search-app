package com.ff.search.model

sealed trait IncidentType extends Product with Serializable

object IncidentType {
  case object Incident extends IncidentType
  case object Problem extends IncidentType
  case object Question extends IncidentType
  case object Task extends IncidentType
}
