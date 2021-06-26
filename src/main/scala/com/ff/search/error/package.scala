package com.ff.search

package object error {
  type ErrorOr[+A] = Either[AppError, A]
}
