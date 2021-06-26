package com.ff.search.index

import cats.Applicative
import cats.effect.Ref
import cats.syntax.all._

class IndexManager[F[_]: Applicative, A <: Document[T], T](indexRef: Ref[F, Index[A]]) {
  def addDocument(document: A): F[Unit] = {
    indexRef.getAndUpdate { index =>
      val _ = index.documents.addOne(document.id -> document)
      index
    } *> Applicative[F].unit
  }
}
