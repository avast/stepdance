package com.avast.steps.examples

import com.avast.steps.StepsBuilder._

/**
 * Monadic Character & For Loop
 */
class Example14 extends StepDanceExample {

  /**
   * "flatMap" illustration - resilience to invalid steps
   */
  def example() {
    val scanner =
      for (src <- steps(List(source, "INVALID_PATH", source2));
           line <- openScanner(src)) yield "//" + line

    for (line <- scanner) {
      println(line)
    }
  }

}
