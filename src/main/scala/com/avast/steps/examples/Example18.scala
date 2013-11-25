package com.avast.steps.examples

import com.avast.steps.{NextStep, NoStep, Step}
import java.io.{FileReader, BufferedReader}

/**
 * Advanced Use - Using Step API directly
 */
class Example18 extends StepDanceExample {

  /**
   * a simple scanner
   */
  def example() {

    val scanner = new (() => Step[String]) {
      lazy val input = new BufferedReader(
        new FileReader(sourceFile))

      def apply(): Step[String] = input.readLine() match {
        case null => NoStep
        case line => NextStep(this, line, input.close())
      }

    }

    for (line <- scanner()) {
      println(line)
    }
  }

}
