package com.avast.steps.examples

import com.avast.steps.{NextStep, FinalStep, NoStep, Step}
import java.io.{FileReader, BufferedReader, Closeable}

/**
 * User: zslajchrt
 * Date: 11/27/13
 * Time: 8:50 PM
 */
object IteratorVsSteps extends StepDanceExample {

  /**
   * stateless
   */
  class Iterator2Steps[T](iterator: Iterator[T]) extends (() => Step[T]) {

    def apply(): Step[T] = {
      if (!iterator.hasNext)
        NoStep
      else {
        val n = iterator.next()
        if (!iterator.hasNext)
          FinalStep(n)
        else
          NextStep(this, n)
      }
    }

  }

  val steps = new Iterator2Steps(List("Prague", "London").iterator)
  for (city <- steps()) {
    println(city)
  }

  /**
   * stateful
   */
  class Steps2Iterator[T](steps: () => Step[T]) extends Iterator[T] with Closeable {

    private[this] var step: Step[T] = null

    def hasNext: Boolean = {
      if (step == null) {
        step = steps()
      }
      step != NoStep
    }

    def next(): T = {
      if (!hasNext) throw new NoSuchElementException()
      try {
        step.value.get
      }
      finally {
        step = step match {
          case NoStep => NoStep // should get here
          case FinalStep(_) => NoStep
          case NextStep(nextStepFn, _) => nextStepFn()
        }
      }
    }

    def close() {
      hasNext // lazy init
      step match {
        case ns: NextStep[T] => ns.close()
        case _ =>
      }
    }

  }

  val iterator = new Steps2Iterator(steps)


  // Iterators are not reusable, while the steps can be!

  val scanner = () => {

    def processLine(input: BufferedReader): Step[String] = {
      input.readLine() match {
        case null => NoStep
        case line => NextStep(() => processLine(input), line, input.close())
      }
    }

    val input = new BufferedReader(new FileReader(sourceFile))
    processLine(input)
  }

  for (s <- scanner()) {
    println(">" + s)
  }

  for (s <- scanner()) {
    println("<" + s)
  }

  // To achieve the same effect with iterators you need to implement an iterator and the function creating
  // a new instance of it. So you will end up with 2 classes and 3 methods that you have to implement.
  // In StepDance you have just 1 class and 1 method.

  def example(): Unit = {}
}

