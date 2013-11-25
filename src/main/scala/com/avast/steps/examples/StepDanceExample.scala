package com.avast.steps.examples

import java.io.{InputStreamReader, BufferedReader}
import com.avast.steps.StepsBuilder._
import com.avast.steps.examples.GoogleResults._

import scala.Predef._
import com.avast.steps.{Step, NoStep}
import java.net.{URLEncoder, URL}
import com.google.gson.Gson


/**
 * User: zslajchrt
 * Date: 11/17/13
 * Time: 4:37 PM
 */
abstract class StepDanceExample {

  val shakespeareRoot = "/Users/zslajchrt/Documents/Projects/Avast/trunk/clockwork/src/test/data/shakespeare/"
  val sourceFile = shakespeareRoot + "README"
  val sourceFile2 = shakespeareRoot + "glossary"
  val source = "file://" + sourceFile
  val source2 = "file://" + sourceFile2
  val webSites = List("http://www.lidovky.cz", "http://www.idnes.cz")

  def example()

  def openScanner(source: String) = {
    try {
      lazy val input = new BufferedReader(new InputStreamReader(new URL(source).openStream()))
      buildSteps {
        input.readLine()
      }.closeWith {
        input.close()
      }.build()
    }
    catch {
      case t: Throwable => {
        t.printStackTrace()
        NoStep
      }
    }
  }

  val hrefPattern = "href=\"(http://.*?)\"".r

  object LinksExtractor {
    def unapply(line: String): Option[Step[String]] = {
      extractLinks(line) match {
        case NoStep => None
        case links => Some(links)
      }
    }

    def extractLinks(line: String) = steps((hrefPattern findAllIn line).matchData.map(_.group(1)))
  }


  //*******  Advanced Use (Google Blog Search, constructing steps directly with Step API) *******

  // Google Blog Search example

  def googleSearch(query: String, page: Int): Step[Result] = {
    val referrer = "http://www.iquality.org/chaplin/"
    val address = "https://ajax.googleapis.com/ajax/services/search/blogs?v=1.0&start=" + page + "&q="
    val charset = "UTF-8"

    val url = new URL(address + URLEncoder.encode(query, charset))

    val connection = url.openConnection()
    connection.addRequestProperty("Referer", referrer)
    val reader = new BufferedReader(new InputStreamReader(connection.getInputStream, charset))

    val resultsJSON = try {
      new Gson().fromJson(reader, classOf[GoogleResults])
    }
    finally {
      reader.close()
    }

    if (resultsJSON.getResponseStatus == 200) {
      val results = resultsJSON.getResponseData.getResults
      buildSteps(results)
        .closeWith {
      }.build()
    } else {
      println("Response code: " + resultsJSON.getResponseStatus + " Details: " + resultsJSON.getResponseDetails)
      NoStep
    }
  }


  def openScanner[C](src: String, context: Option[C]): Step[(String, Option[C])] = {
    try {
      lazy val input = new BufferedReader(new InputStreamReader(new URL(src).openStream()))
      buildSteps {
        val line: String = input.readLine()
        if (line == null) null else (line, context)
      }.closeWith {
        input.close()
      }.build()
    }
    catch {
      case t: Throwable => {
        System.err.println("Cannot open source: " + t.getMessage)
        NoStep
      }
    }
  }
}
