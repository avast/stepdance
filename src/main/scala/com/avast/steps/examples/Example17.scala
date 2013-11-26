package com.avast.steps.examples

import com.avast.steps.StepsBuilder._

/**
 * Advanced Use - Google Blog Search
 */
class Example17 extends StepDanceExample {

  def example() {

    val query = "Sobotka ČSSD"
    val inContent = "Sobotka"

    lazy val lines =
      for (page <- steps(0 until 3);
           searchResult <- googleSearch(query, page);
           pageLine <- openScanner(searchResult.getBlogUrl, Some(searchResult))
           if pageLine._1.contains(inContent)
      ) yield pageLine

    for (line <- lines) {
      println(line._2.get.getPostUrl + ": " + line._1)
    }

  }

}
