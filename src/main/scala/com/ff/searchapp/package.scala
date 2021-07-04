package com.ff

import cats.Show
import cats.syntax.show._

import java.util.Locale

package object searchapp {
  def vectorShow[A: Show]: Show[Vector[A]] = _.map(_.show).mkString("[", ",", "]")

  def optionShow[A: Show]: Show[Option[A]] = {
    case Some(a) => a.show
    case None => "null"
  }

  def compareStringEquals(value1: String, value2: String): Boolean =
    value1.toLowerCase(Locale.ENGLISH) == value2.toLowerCase(Locale.ENGLISH)

  def compareStringContainsAnother(value: String, another: String): Boolean =
    value.toLowerCase(Locale.ENGLISH).contains(another.toLowerCase(Locale.ENGLISH))
}
