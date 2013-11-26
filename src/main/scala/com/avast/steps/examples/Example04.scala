package com.avast.steps.examples

import com.avast.steps.NoStep


/**
 * StepDance: Basic Usage
 */
class Example04 extends StepDanceExample {

  /**
   * Connecting steps conditionally
   */
  def example() {
    val scanner = openScanner(source)
      .connect(lastStep => {
      if (Some("--------") == lastStep.value)
        openScanner(source2)
      else NoStep
    })

    for (line <- scanner) {
      println(line)
    }
  }

}
