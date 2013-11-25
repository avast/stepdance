package com.avast.steps.examples

import java.io.{File, Closeable, FileReader, BufferedReader}
import com.avast.steps.StepsBuilder._
import scala.io.{BufferedSource, Source}

/**
 * StepDance: Basic Usage
 */
class Example00 extends StepDanceExample {

  def example_() {
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


  def example() {
    //*** The producer side
    lazy val sources = List(sourceFile, "blabla", sourceFile2).iterator

    val scanner: Iterator[String] =
      for (src <- sources;
           s <- Source.fromFile(src).getLines())
      yield s

    //*** The consumer side
    for (line <- scanner) {
      println(line)
    }

  }

  def example___() {
    //*** The producer side
    lazy val input = new BufferedReader(new FileReader(sourceFile))

    val scannerBasic = () => {
      input.readLine()
    }

    //*** The consumer side calls the scanner until it returns null
    var line: String = null
    do {
      line = scannerBasic()
      if (line != null)
        println(line)
    } while (line != null)

  }


}
