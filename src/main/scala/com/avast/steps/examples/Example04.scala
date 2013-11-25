package com.avast.steps.examples

import com.avast.steps.NoStep


/**
 * StepDance: Basic Usage
 */
class Example04 extends StepDanceExample {

  /**
   * Connecting steps
   */
  def example() {
    val scanner = openScanner(source)
      .connect(s => if (s.value == Some("--------")) {
      openScanner(source2)
    } else NoStep)
      .map("//" + _)

    for (line <- scanner) {
      println(line)
    }
  }


}
