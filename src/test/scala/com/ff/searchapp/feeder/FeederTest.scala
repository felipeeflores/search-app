package com.ff.searchapp.feeder

import cats.effect.IO
import cats.effect.kernel.Ref
import com.ff.searchapp.error.AppError.InvalidRecord
import com.ff.searchapp.error.{AppError, ErrorOr}
import fs2.{Pipe, Stream}
import io.circe.fs2._
import io.circe.literal.JsonStringContext
import io.circe.syntax.EncoderOps
import io.circe.{DecodingFailure, Json}
import org.specs2.matcher.IOMatchers
import org.specs2.mutable.Specification

class FeederTest extends Specification with IOMatchers {

  val rawData =
    json"""
          [
           {"_id": 1},
           {"_id": 2}
          ]
          """

  "Feeder" should {
    "extract raw data" in {
      val extractedValues = Ref.of[IO, List[Json]](List.empty).flatMap { ref =>
        def parseExtractedData: Pipe[IO, Byte, Json] = input => {
          val recordExtraction = input
            .through(byteArrayParser)
            .compile
            .toList
            .flatMap(ref.set)
          Stream.eval(recordExtraction) >> Stream.empty
        }
        val feeder = new Feeder[IO, Unit, Unit](
          extract = extractData,
          parse = parseExtractedData,
          decode = _ => Right(()),
          transform = identity,
          load = _ => IO.unit,
          handleError = _ => IO.unit
        )
        feeder.feed("foo") *> ref.get
      }

      val expectedExtractedValues = Vector(
        Json.obj("_id" -> 1.asJson),
        Json.obj("_id" -> 2.asJson)
      )

      extractedValues must returnValue(expectedExtractedValues)
    }

    "decode raw data to expected type" in {
      val decodedData = Ref.of[IO, List[Int]](List.empty).flatMap { ref =>
        def decodeJson(json: Json): ErrorOr[Int] = {
          val decoded = for {
            int <- json.hcursor.get[Int]("_id")
          } yield int
          decoded.left.map(_ => InvalidRecord(json, "fail"))
        }
        val feeder = new Feeder[IO, Int, Int](
          extract = extractData,
          parse = byteArrayParser,
          decode = decodeJson,
          transform = identity,
          load = int => ref.getAndUpdate(_ :+ int) *> IO.unit,
          handleError = _ => IO.unit
        )
        feeder.feed("foo") *> ref.get
      }

      val expectedDecodedValues = Vector(1, 2)

      decodedData must returnValue(expectedDecodedValues)
    }

    "transform decoded data to expected type" in {
      val transformedData = Ref.of[IO, List[String]](List.empty).flatMap { ref =>
        def decodeJson(json: Json): ErrorOr[Int] = {
          val decoded = for {
            int <- json.hcursor.get[Int]("_id")
          } yield int
          decoded.left.map(_ => InvalidRecord(json, "fail"))
        }
        val feeder = new Feeder[IO, Int, String](
          extract = extractData,
          parse = byteArrayParser,
          decode = decodeJson,
          transform = _.toString,
          load = int => ref.getAndUpdate(_ :+ int) *> IO.unit,
          handleError = _ => IO.unit
        )
        feeder.feed("foo") *> ref.get
      }

      val expectedTransformedValues = Vector("1", "2")

      transformedData must returnValue(expectedTransformedValues)
    }

    "load correct data" in {
      val loadedData = Ref.of[IO, List[Int]](List.empty).flatMap { ref =>
        def decodeJson(json: Json): ErrorOr[Int] = {
          val decoded = for {
            int <- json.hcursor.get[Int]("_id")
            value <- if (int == 2) Left(DecodingFailure("foo", List.empty)) else Right(int)
          } yield value
          decoded.left.map(_ => InvalidRecord(json, "fail"))
        }
        val feeder = new Feeder[IO, Int, Int](
          extract = extractData,
          parse = byteArrayParser,
          decode = decodeJson,
          transform = identity,
          load = int => ref.getAndUpdate(_ :+ int) *> IO.unit,
          handleError = _ => IO.unit
        )
        feeder.feed("foo") *> ref.get
      }

      val expectedTransformedValues = Vector(1)

      loadedData must returnValue(expectedTransformedValues)
    }

    "handle errors" in {
      val loadedErrors = Ref.of[IO, List[AppError]](List.empty).flatMap { ref =>
        def decodeJson(json: Json): ErrorOr[Int] = {
          val decoded = for {
            int <- json.hcursor.get[Int]("_id")
            value <- if (int == 2) Left(DecodingFailure("foo", List.empty)) else Right(int)
          } yield value
          decoded.left.map(_ => InvalidRecord(json, "fail"))
        }
        val feeder = new Feeder[IO, Int, Int](
          extract = extractData,
          parse = byteArrayParser,
          decode = decodeJson,
          transform = identity,
          load = _ => IO.unit,
          handleError = appError => ref.getAndUpdate(_ :+ appError) *> IO.unit
        )
        feeder.feed("foo") *> ref.get
      }
      val rawJson = Json.obj("_id" -> 2.asJson)
      val expectedErrors = Vector(InvalidRecord(rawJson, "fail"))

      loadedErrors must returnValue(expectedErrors)
    }
  }

  private val extractData: (String) => Stream[IO, Byte] = _ => Stream.iterable(rawData.noSpaces.getBytes)
}
