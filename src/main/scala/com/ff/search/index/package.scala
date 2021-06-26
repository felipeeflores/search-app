package com.ff.search

import com.ff.search.index.Document.{TicketDocument, UserDocument}

package object index {
  type UserIndex = Index[UserDocument]
  type TicketIndex = Index[TicketDocument]
}
