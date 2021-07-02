package com.ff.searchapp.search.query

sealed trait Filter extends Product with Serializable

object Filter {
  final case class TextFilter(fieldName: String, operator: Operator, value: String) extends Filter
}
