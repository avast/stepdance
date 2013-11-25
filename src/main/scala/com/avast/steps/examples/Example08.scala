package com.avast.steps.examples

/**
 * StepDance: Basic Usage
 */
class Example08 extends StepDanceExample {

  /**
   * Folding steps - prepend the line number to the line
   */
  def example() {
    val scanner = openScanner(source)
      .fold((-1, ""))((len, line) => (len._1 + 1, line))

    for (line <- scanner) {
      println(line._1 + ":" + line._2)
    }

  }

}
