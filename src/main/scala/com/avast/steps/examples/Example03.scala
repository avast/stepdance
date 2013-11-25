package com.avast.steps.examples

import java.io.{FileReader, BufferedReader}
import com.avast.steps.StepsBuilder._

/**
 * StepDance: Basic Usage
 */
class Example03 extends StepDanceExample {

  /**
   * Decoration of the step functions
   */
  def example() {
    // create a lazy scanner
    var counter: Long = 0
    lazy val input = new BufferedReader(new FileReader(sourceFile))
    val scanner = buildSteps {
      input.readLine()
    }.finishWith {
      input.close()
      println("Closed")
    }.build()
      .decorate(decorStepFn => {
      counter += 1
      decorStepFn()
    })

    // scan
    for (line <- scanner) {
      println(line)
    }

    println("Lines read " + counter)
  }

}
