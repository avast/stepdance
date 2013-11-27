package com.avast.steps.examples

import com.avast.steps.StepsBuilder._

/**
 * Monadic Character & For Loop
 */
class Example15 extends StepDanceExample {

  /**
   * Chaining flatMaps
   */
  def example() {
    val scanner = for (src <- steps(webSites);
                       pageLine <- openScanner(src);
                       link <- LinksExtractor.extractLinks(pageLine) if !link.contains("lidovky"))
    yield link

    for (link <- scanner) {
      println(link)
    }
  }

}
