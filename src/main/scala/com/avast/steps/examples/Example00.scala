package com.avast.steps.examples

import java.io.{File, Closeable, FileReader, BufferedReader}
import com.avast.steps.StepsBuilder._
import scala.io.{BufferedSource, Source}

/**
 * StepDance: Basic Usage
 */
class Example00 extends StepDanceExample {

  def example() {
    lazy val input =
      new BufferedReader(new FileReader(sourceFile))

    val scanner = steps {
      input.readLine()
    }

    for (line <- scanner) {
      println(line)
    }
  }

}
