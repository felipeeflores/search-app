package com.ff.searchapp

package object error {
  type ErrorOr[+A] = Either[AppError, A]
}
