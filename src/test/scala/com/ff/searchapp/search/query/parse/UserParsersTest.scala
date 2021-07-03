package com.ff.searchapp.search.query.parse

import atto.Atto._
import com.ff.searchapp.search.query.Filter.{BooleanFilter, IntFilter, TextFilter}
import com.ff.searchapp.search.query.Operator
import com.ff.searchapp.search.query.SearchField.UserSearchFields.{UserIdField, UsernameField, VerifiedField}
import org.specs2.mutable.Specification

class UserParsersTest extends Specification {

  "UserParsers" should {
    "parse user filters" in {
      "id" in {
        val rawFilter = "id==123"
        val expectedFilter = Vector(IntFilter(UserIdField, Operator.EQUALS, 123))

        val result = UserParsers.userQueryFiltersParser.parseOnly(rawFilter).either

        result must beRight(expectedFilter)
      }

      "username" in {
        val rawFilter = "username==jason"
        val expectedFilter = Vector(TextFilter(UsernameField, Operator.EQUALS, "jason"))

        val result = UserParsers.userQueryFiltersParser.parseOnly(rawFilter).either

        result must beRight(expectedFilter)
      }

      "verified" in {
        val rawFilter = "verified==true"
        val expectedFilter = Vector(BooleanFilter(VerifiedField, Operator.EQUALS, value = true))

        val result = UserParsers.userQueryFiltersParser.parseOnly(rawFilter).either

        result must beRight(expectedFilter)
      }

      "all filters together" in {
        val rawFilter = "id==123 username==jason verified==true"
        val expectedFilter = Vector(
          IntFilter(UserIdField, Operator.EQUALS, 123),
          TextFilter(UsernameField, Operator.EQUALS, "jason"),
          BooleanFilter(VerifiedField, Operator.EQUALS, value = true)
        )

        val result = UserParsers.userQueryFiltersParser.parseOnly(rawFilter).either

        result must beRight(expectedFilter)
      }
    }
  }
}
