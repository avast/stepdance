package com.avast.steps.examples

import java.io.{FileReader, BufferedReader}
import com.avast.steps.StepsBuilder._

/**
 * StepDance: Basic Usage
 */
class Example00 extends StepDanceExample {

  def example() {
    //*** The producer side
    lazy val input = new BufferedReader(new FileReader(sourceFile))
    val scanner = steps {
      input.readLine()
    }

    //*** The consumer side
    for (line <- scanner) {
      println(line)
    }
  }

  /*
   * Notes:
   * The last null value returned by the input reader
   * is transformed to NoStep (an analogy to None).
   * Therefore no null-check is necessary.
   */

}
