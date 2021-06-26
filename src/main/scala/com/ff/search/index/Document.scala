package com.ff.search.index

import com.ff.search.model.User

sealed trait Document[+A] extends Product with Serializable {
  val id: DocumentId
  val data: A
}

object Document {

  final case class UserDocument(id: DocumentId, name: String, data: User) extends Document[User]
}
