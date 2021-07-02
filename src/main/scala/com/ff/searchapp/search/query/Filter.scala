package com.ff.searchapp.search.query

sealed trait Filter extends Product with Serializable

object Filter {
  final case class TextFilter(fieldName: String, operator: Operator, value: String) extends Filter
  final case class SumTypeFilter[S](fieldName: String, operator: Operator, value: S) extends Filter
  final case class OptionalField(fieldName: String, operator: Operator, value: Option[String]) extends Filter
  final case class MultipleTextField(fieldName: String, operator: Operator, values: Vector[String]) extends Filter
}
