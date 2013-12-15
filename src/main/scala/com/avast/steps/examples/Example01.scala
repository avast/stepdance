package com.avast.steps.examples

import java.io.{IOException, FileReader, BufferedReader}
import com.avast.steps.StepsBuilder._
import com.avast.steps.{NoStep, Step}
import java.net.{URL, MalformedURLException}

/**
 * StepDance: Basic Usage
 */
class Example01 extends StepDanceExample {

  /**
   * Using the step builder to specify the finisher
   */
  def example() {

    // Create the failing scanner
    def newScanner(src: String): Step[String] = {
      lazy val input =
        new BufferedReader(new FileReader(src))

      buildSteps {
        val line: String = input.readLine()
        if (line == "Histories" || line.contains("WOOLWARD")) {
          throw new Exception("ERROR:" + line)
        }
        line
      }.closeWith {
        println("CLOSED")
        input.close()
      }.build()
    }


    val robustScanner: Step[String] = for (src <- steps(List(sourceFile, sourceFile2));
                                           line <- buildSteps(newScanner(src)).handleErrorsWith {
                                             case t: Throwable => {
                                               println("Problem with file: " + t.getMessage)
                                             }
                                           }.build()) yield line

    // No need for try/finally to close the input,
    // it is done automatically behind the scenes
    //for (line <- scanner) {
    for (line <- robustScanner) {
      //      if (line == "")
      //        throw new RuntimeException("EXCEPTION")
      //println(line)
    }
  }

}
