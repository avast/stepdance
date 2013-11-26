package com.avast.steps.examples

import com.avast.steps.StepsBuilder._

/**
 * StepDance: Basic Usage
 */
class Example05 extends StepDanceExample {

  /**
   * Connecting steps of diverse origin
   */
  def example() {
    val scanner = openScanner(source)
      .connect(_ => steps(List("******************")))
      .connect(_ => openScanner(source2))

    for (line <- scanner) {
      println(line)
    }
  }

}
