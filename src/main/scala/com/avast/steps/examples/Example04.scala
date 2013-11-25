package com.avast.steps.examples


/**
 * StepDance: Basic Usage
 */
class Example04 extends StepDanceExample {

  /**
   * Connecting steps
   */
  def example() {
    val scanner = openScanner(source)
      .connect(_ => openScanner(source2))
      .map("//" + _)

    for (line <- scanner) {
      println(line)
    }
  }


}
