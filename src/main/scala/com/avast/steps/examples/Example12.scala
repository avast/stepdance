package com.avast.steps.examples

/**
 * Monadic Character & For Loop
 */
class Example12 extends StepDanceExample {

  /**
   * "withFilter" illustration
   */
  def example() {
    //openScanner(source).withFilter(_.contains("Henry"))

    val scanner = for (line <- openScanner(source)
                       if line.contains("Henry"))
    yield "//" + line

    for (line <- scanner) {
      println(line)
    }
  }

}
