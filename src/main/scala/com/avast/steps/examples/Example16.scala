package com.avast.steps.examples

import com.avast.steps.StepsBuilder._

/**
 * Monadic Character & For Loop
 */
class Example16 extends StepDanceExample {
  import LinksExtractor._

  /**
   * chaining flatMaps with a filter
   */
  def example() {
    val scanner = for (src <- steps(webSites);
                       pageLine <- openScanner(src);
                       link <- extractLinks(pageLine);
                       line <- openScanner(link)
                       if line.contains("Zeman")
                         || line.contains("Babiš")
                         || line.contains("Sobotka"))
    yield line

    for (line <- scanner) {
      println(line)
    }
  }

}
