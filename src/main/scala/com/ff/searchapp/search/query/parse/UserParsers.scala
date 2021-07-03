package com.ff.searchapp.search.query.parse

import atto.Atto._
import atto._
import com.ff.searchapp.search.query.Filter
import com.ff.searchapp.search.query.SearchField.UserSearchFields.{UserIdField, UsernameField, VerifiedField}
import com.ff.searchapp.search.query.parse.Parsers._

object UserParsers {

  private val oneQueryFilterParser: Parser[Filter] =
    intFieldParser(UserIdField) | textFieldParser(UsernameField) | booleanFieldParser(VerifiedField)

  val userQueryFiltersParser: Parser[Vector[Filter]] = many(oneQueryFilterParser).map(_.toVector)
}
