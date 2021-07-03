package com.ff.searchapp.feeder

import com.ff.searchapp.index.Document.UserDocument
import com.ff.searchapp.index.DocumentId
import com.ff.searchapp.model.User

object TransformToUserDocument {

  def apply(user: User): UserDocument = {
    UserDocument(
      DocumentId(user.id.value.toString),
      data = user
    )
  }
}
