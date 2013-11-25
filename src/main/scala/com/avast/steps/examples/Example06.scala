package com.avast.steps.examples

import com.avast.steps.StepsBuilder._

/**
 * StepDance: Basic Usage
 */
class Example06 extends StepDanceExample {

  /**
   * Mapping steps individually
   */
  def example() {
    val scanner = openScanner(source).map(">>" + _)
      .connect(_ => steps(List("******************")))
      .connect(_ => openScanner(source2).map("<<" + _))

    for (line <- scanner) {
      println(line)
    }
  }

}
