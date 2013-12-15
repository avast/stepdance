package com.avast.steps.examples

import com.avast.steps.Step

/**
 * Monadic Character & For Loop
 */
class Example11 extends StepDanceExample {

  /**
   * "map" illustration
   */
  def example() {
    //val scanner: Step[String] = openScanner(source).map("//" + _)

    val scanner = for (line <- openScanner(source))
    yield "//" + line

    for (line <- scanner) {
      println(line)
    }
  }

}
