package com.ff.search.feeder

import com.ff.search.index.Document.UserDocument
import com.ff.search.index.DocumentId
import com.ff.search.model.User

object TransformToUserDocument {

  def apply(user: User): UserDocument = {
    UserDocument(
      DocumentId(user.id.value.toString),
      name = user.name.value,
      data = user
    )
  }
}
