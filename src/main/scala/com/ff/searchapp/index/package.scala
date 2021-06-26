package com.ff.searchapp

import com.ff.searchapp.index.Document.{TicketDocument, UserDocument}

package object index {
  type UserIndex = Index[UserDocument]
  type TicketIndex = Index[TicketDocument]
}
