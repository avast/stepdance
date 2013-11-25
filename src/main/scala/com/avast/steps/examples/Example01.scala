package com.avast.steps.examples

import java.io.{FileReader, BufferedReader}
import com.avast.steps.StepsBuilder._

/**
 * StepDance: Basic Usage
 */
class Example01 extends StepDanceExample {

  /**
   * Using the step builder to specify the finisher
   */
  def example() {
    // create a lazy scanner
    lazy val input = new BufferedReader(new FileReader(sourceFile))
    val scanner = buildSteps {
      input.readLine()
    }.closeWith {
      input.close()
    }.build()

    // scan
    try {
      for (line <- scanner) {
        println(line)
      }
    }
    finally {
      scanner.close()
    }
  }

}
