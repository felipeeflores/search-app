package com.ff.search.model

import io.circe.Decoder

sealed trait IncidentType extends Product with Serializable

object IncidentType {
  case object Incident extends IncidentType
  case object Problem extends IncidentType
  case object Question extends IncidentType
  case object Task extends IncidentType
  case object Other extends IncidentType

  implicit val incidentTypeDecoder: Decoder[IncidentType] = c =>
    c.as[String].flatMap {
      case "incident" => Right(Incident)
      case "problem" => Right(Problem)
      case "question" => Right(Question)
      case "task" => Right(Task)
      case other => Right(Other)
    }
}
