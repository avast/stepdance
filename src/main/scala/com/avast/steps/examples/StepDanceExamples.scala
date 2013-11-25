package com.avast.steps.examples


/**
 * User: zslajchrt
 * Date: 11/23/13
 * Time: 9:03 PM
 */
object StepDanceExamples {

  val examples = List(
    new Example00,
    new Example01,
    new Example02,
    new Example03,
    new Example04,
    new Example05,
    new Example06,
    new Example07,
    new Example08,
    new Example09,
    new Example10,
    new Example11,
    new Example12,
    new Example13,
    new Example14,
    new Example15,
    new Example16,
    new Example17,
    new Example18,
    new Example19
  )

  def main(args: Array[String]) {
    print("Type example number:")
    val exNum = readInt()
    if (exNum >= 0 && exNum < examples.size) {
      examples(exNum).example()
    } else {
      print("Invalid example number. 0-" + (examples.size - 1))
    }
  }

}
