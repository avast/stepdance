package com.avast.steps.examples

import java.io.{FileReader, BufferedReader}
import com.avast.steps.StepsBuilder._
import com.avast.steps.{NoStep, Step}

/**
 * StepDance: Basic Usage
 */
class Example01 extends StepDanceExample {

  /**
   * Using the step builder to specify the finisher
   */
  def example() {

    // Create a lazy scanner
    lazy val input =
      new BufferedReader(new FileReader(sourceFile))
    val scanner = buildSteps {
      input.readLine()
    }.handleErrorsWith {
      case t =>
        println("Error: " + t.getMessage) // will possibly continue with the next sequence
        //throw t // will finish
    }.closeWith {
      println("CLOSED")
      input.close()
    }.build()

    // No need for try/finally to close the input,
    // it is done automatically behind the scenes
    for (line <- scanner) {
      if (line == "")
        throw new RuntimeException("EXCEPTION")
      println(line)
    }
  }

}
