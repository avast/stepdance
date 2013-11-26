package com.avast.steps.examples

/**
 * StepDance: Basic Usage
 */
class Example10 extends StepDanceExample {

  /**
   * Ignoring the first N steps and stopping after M steps
   */
  def example() {
    val scanner = openScanner(source)
      .foldLeft((-1, ""))((len, line) => (len._1 + 1, line))
      .withFilter(_._1 > 3)
      .stopAt(_._1 == 10)

    for (line <- scanner) {
      println(line._1 + ":" + line._2)
    }
  }

}
