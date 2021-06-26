package com.ff.searchapp.index

import scala.collection.mutable

final case class Index[A](documents: mutable.HashMap[DocumentId, A])
