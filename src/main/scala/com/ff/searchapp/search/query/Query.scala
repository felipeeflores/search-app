package com.ff.searchapp.search.query

final case class Query(searchType: SearchTarget, filters: Vector[Filter])
