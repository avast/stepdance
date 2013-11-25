package com.avast.steps.examples

/**
 * Monadic Character & For Loop
 */
class Example11 extends StepDanceExample {

  /**
   * "map" illustration
   */
  def example() {
    val scanner = for (line <- openScanner(source))
    yield "//" + line

    for (line <- scanner) {
      println(line)
    }
  }

}
