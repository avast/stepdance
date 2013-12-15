package com.avast.steps.examples

import com.avast.steps.StepsBuilder._
import com.avast.steps.Step

/**
 * Monadic Character & For Loop
 */
class Example13 extends StepDanceExample {

  /**
   * "flatMap" illustration
   */
  def example() {
    //val scanner: Step[String] = steps(List(source, source2)).flatMap(src => openScanner(src))

    val scanner = for (src <- steps(List(source, source2));
                       line <- openScanner(src))
    yield "//" + line

    for (line <- scanner) {
      println(line)
    }
  }

}
