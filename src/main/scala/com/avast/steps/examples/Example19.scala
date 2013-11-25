package com.avast.steps.examples

import com.avast.steps.{FinalStep, NextStep, NoStep, Step}
import java.io.{InputStreamReader, BufferedReader}
import java.net.URL

/**
 * Advanced Use - Using Step API directly
 */
class Example19 extends StepDanceExample {

  /**
   * a web crawler
   */
  def example() {

    val scanner = Scanner("http://www.novinky.cz", 4)

    for (line <- scanner() if line.contains("Babiš")) {
      println(line)
    }

  }

  case class Scanner(source: String, maxDepth: Int)
    extends (() => Step[String]) {

    lazy val inputOpt = {
      try {
        //println("Opening (level " + maxDepth + ") " + source)
        Some(new BufferedReader(
          new InputStreamReader(
            new URL(source).openStream())))
      }
      catch {
        case t: Throwable => {
          System.err.println("Cannot open source " + t.getMessage)
          None
        }
      }
    }

    def apply(): Step[String] = inputOpt match {
      case None => NoStep

      case Some(input) => {
        val line: String = input.readLine()
        line match {
          case null => NoStep
          case LinksExtractor(urls) if maxDepth > 0 => {
            // current line + sub-scanners + return
            FinalStep(line).connect(_ => {
              // Create the sub-scanners
              (for (url <- urls;
                    subScanner <- Scanner(url, maxDepth - 1)())
              yield subScanner)
                // return to this level
                .connect(_ => NextStep(this, line, input.close()))
            })
          }
          case _ => {
            NextStep(this, line, input.close())
          }
        }
      }
    }

  }

}
