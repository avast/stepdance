package com.avast.steps.examples

import java.io.{FileReader, BufferedReader}
import com.avast.steps.StepsBuilder._

/**
 * StepDance: Basic Usage
 */
class Example02 extends StepDanceExample {

  /**
   * An illustration of transforming the step results by means of the map method
   */
  def example() {
    // create a lazy scanner
    lazy val input = new BufferedReader(new FileReader(sourceFile))
    val scanner = buildSteps {
      input.readLine()
    }.finishWith {
      input.close()
      println("Closed")
    }.build().map("//" + _)

    // scan
    for (line <- scanner) {
      println(line)
    }
  }

}
