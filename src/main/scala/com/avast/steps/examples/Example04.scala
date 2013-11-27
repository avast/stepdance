package com.avast.steps.examples


/**
 * StepDance: Basic Usage
 */
class Example04 extends StepDanceExample {

  /**
   * Connecting steps conditionally
   */
  def example() {
    val scanner = openScanner(source, errorProne = true)
      .connect(lastStep => {
        openScanner(source2, errorProne = false)
//      if (Some("--------") == lastStep.value)
//        openScanner(source2)
//      else NoStep
    })

    for (line <- scanner) {
//      if (line == "")
//        throw new RuntimeException("EXCEPTION")
      println(line)
    }
  }

}
