package com.avast.steps.examples

import com.avast.steps.StepsBuilder._

/**
 * Monadic Character & For Loop
 */
class Example13 extends StepDanceExample {

  /**
   * "flatMap" illustration
   */
  def example() {
    val scanner = for (src <- steps(List(source, source2));
                       line <- openScanner(src))
    yield "//" + line

    for (line <- scanner) {
      println(line)
    }
  }

}
