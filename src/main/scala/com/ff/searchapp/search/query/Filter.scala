package com.ff.searchapp.search.query

import com.ff.searchapp.model.IncidentType

sealed trait Filter extends Product with Serializable

object Filter {
  final case class TextFilter(searchField: SearchField, operator: Operator, value: String) extends Filter
  final case class IntFilter(searchField: SearchField, operator: Operator, value: Int) extends Filter
  final case class BooleanFilter(searchField: SearchField, operator: Operator, value: Boolean) extends Filter
  final case class IncidentTypeFilter(searchField: SearchField, operator: Operator, value: IncidentType) extends Filter
  final case class OptionalIntFilter(searchField: SearchField, operator: Operator, value: Option[Int]) extends Filter
  final case class MultipleTextFilter(searchField: SearchField, operator: Operator, values: Vector[String]) extends Filter
}
