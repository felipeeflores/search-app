package com.ff.searchapp.feeder

import com.ff.searchapp.error.AppError.ExtractFileError
import fs2.{Collector, text}
import org.specs2.matcher.IOMatchers
import org.specs2.mutable.Specification

class ExtractTest extends Specification with IOMatchers {

  "Extract" should {
    "extract file contents" in {
      Extract("./data/test.json")
        .through(text.utf8Decode)
        .compile
        .to(Collector.string)
        .map(_.filterNot(_.isWhitespace)) must returnValue("""[{"_id":100},{"_id":200}]""")
    }

    "report extraction errors" in {
      Extract("/tmp/not-exists.json")
        .compile
        .toVector
        .attempt must returnValue(Left(ExtractFileError("/tmp/not-exists.json")))
    }
  }
}
