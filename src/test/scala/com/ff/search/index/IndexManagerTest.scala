package com.ff.search.index

import cats.effect.{IO, Ref}
import com.ff.search.index.Document.UserDocument
import com.ff.search.model._
import fs2.Stream
import org.specs2.matcher.IOMatchers
import org.specs2.mutable.Specification

import java.time.OffsetDateTime
import scala.collection.mutable

class IndexManagerTest extends Specification with IOMatchers {

  "IndexManager" should {
    "add documents concurrently" in {
      val indexIO = Ref.of[IO, Index[UserDocument]](Index(documents = mutable.HashMap.empty)).flatMap { ref =>
        val indexManager = new IndexManager[IO, UserDocument, User](ref)
        Stream
          .range(start = 0, stopExclusive = 1000)
          .map { i =>
            val name = s"Name for $i"
            val createdAt = OffsetDateTime.parse(s"2021-06-26T19:45:49.$i+10:00")
            UserDocument(
              id = DocumentId(i.toString),
              name = name,
              data = User(UserId(i), Username(name), createdAt, verified = true)
            )
          }
          .lift[IO]
          .parEvalMap(maxConcurrent = 4)(indexManager.addDocument)
          .compile
          .drain
          .flatMap(_ => ref.get)
      }

      indexIO.map(_.documents.size) must returnValue(1000)
    }
  }
}
