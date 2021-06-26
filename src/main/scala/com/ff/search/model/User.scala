package com.ff.search.model

import java.time.OffsetDateTime

final case class User(id: UserId, name: Username, createdAt: OffsetDateTime, verified: Boolean)
