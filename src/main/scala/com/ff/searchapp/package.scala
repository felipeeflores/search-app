package com.ff

import cats.Show
import cats.syntax.show._

package object searchapp {
  def vectorShow[A: Show]: Show[Vector[A]] = _.map(_.show).mkString("[", ",", "]")

  def optionShow[A: Show]: Show[Option[A]] = {
    case Some(a) => a.show
    case None => "null"
  }
}
