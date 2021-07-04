package com.ff.searchapp.feeder

import com.ff.searchapp.TestFixture.sampleOffsetDateTime
import com.ff.searchapp.index.Document.UserDocument
import com.ff.searchapp.index.DocumentId
import com.ff.searchapp.model.{User, UserId, Username}
import org.specs2.mutable.Specification

class TransformToUserDocumentTest extends Specification {

  "TransformToUserDocumentTest" should {
    // given more time this could easily be a property based testing spec
    "transform user to user document" in {
      val aUser = User(
        UserId(1),
        Username("John Kaine"),
        createdAt = sampleOffsetDateTime,
        verified = false
      )
      TransformToUserDocument(aUser) must beEqualTo(
        UserDocument(
          id = DocumentId("1"),
          data = aUser
        )
      )
    }
  }
}
