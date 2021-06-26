package com.ff.search.feeder

import fs2.{text, Collector}
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
  }
}
