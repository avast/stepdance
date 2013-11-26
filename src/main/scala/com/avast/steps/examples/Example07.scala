package com.avast.steps.examples

/**
 * StepDance: Basic Usage
 */
class Example07 extends StepDanceExample {

  /**
   * Filtering steps
   */
  def example() {
    val scanner = openScanner(source)
      .withFilter(_.contains("Henry"))

    for (line <- scanner) {
      println(line)
    }
  }

}
