package com.avast.steps.examples

/**
 * StepDance: Basic Usage
 */
class Example09 extends StepDanceExample {

  /**
   * Stopping steps
   */
  def example() {
    val scanner = openScanner(source)
      .foldLeft((-1, ""))((len, line) => (len._1 + 1, line))
      .stopAt(_._1 == 10)

    for (line <- scanner) {
      println(line._1 + ":" + line._2)
    }
  }

}
