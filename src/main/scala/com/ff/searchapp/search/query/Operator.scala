package com.ff.searchapp.search.query

sealed trait Operator extends Product with Serializable

object Operator {
  case object EQUALS extends Operator
  case object LIKE extends Operator
  case object IN extends Operator
}
