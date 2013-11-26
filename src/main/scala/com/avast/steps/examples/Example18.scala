package com.avast.steps.examples

import com.avast.steps.{NextStep, NoStep, Step}
import java.io.{FileReader, BufferedReader}

/**
 * Advanced Use - Using StepDance API directly
 */
class Example18 extends StepDanceExample {

  /**
   * a simple scanner
   */
  def example() {

    // A stateless (functional) alternative to an iterator
    // See bellow to the traditional Iterator approach
    // In contrast to Iterator, we have to implement only one method,
    // also the code is more concise.
    // Yet, there is no state (except the input reader)!
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

  def exampleUsingIterator() {
    // The hasNext method is usually ugly. In general,
    // iterators tend to concentrate its logic in
    // hasNext, which is supposed to be, intuitively,
    // a simple method (and idempotent, like all getters)

    val scanner = new Iterator[String] {
      lazy val input = new BufferedReader(
        new FileReader(sourceFile))

      var line: String = null

      private def nextLine() {
        line = input.readLine()
      }

      def hasNext: Boolean = {
        if (line == null) {
          nextLine()
        }
        line != null
      }

      def next(): String = {
        // We have to comply with the contract and check the hasNext first!
        if (!hasNext)
          throw new NoSuchElementException
        try {
          line
        }
        finally {
          // move ahead
          nextLine()
        }
      }
    }

    for (line <- scanner) {
      println(line)
    }

  }

}
