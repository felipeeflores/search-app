package com.ff.searchapp.search.query

import com.ff.searchapp.model.IncidentType

sealed trait Filter extends Product with Serializable

object Filter {
  final case class TextFilter(fieldName: String, operator: Operator, value: String) extends Filter
  final case class IntFilter(fieldName: String, operator: Operator, value: Int) extends Filter
  final case class BooleanFilter(fieldName: String, operator: Operator, value: Boolean) extends Filter
  final case class IncidentTypeFilter(fieldName: String, operator: Operator, value: IncidentType) extends Filter
  final case class OptionalIntFilter(fieldName: String, operator: Operator, value: Option[Int]) extends Filter
  final case class MultipleTextFilter(fieldName: String, operator: Operator, values: Vector[String]) extends Filter
}
