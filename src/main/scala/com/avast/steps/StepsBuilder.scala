package com.avast.steps

import scala.collection.JavaConversions._
import scala._

/**
 * User: zslajchrt
 * Date: 11/25/13
 * Time: 11:34 PM
 */
object StepsBuilder {

  val defaultErrorHandler: PartialFunction[Throwable, Unit] = { case t => throw t }

  def steps[T >: Null](rawStepFn: => T) = steps_(rawStepFn, () => {}, defaultErrorHandler)

  def steps[T](iterable: java.lang.Iterable[T]) = steps_(iterable.iterator, () => {}, defaultErrorHandler)

  def steps[T](iterable: Iterator[T]) = steps_(asJavaIterator(iterable), () => {}, defaultErrorHandler)

  def steps[T](iterable: Iterable[T]) = steps_(asJavaIterator(iterable.iterator), () => {}, defaultErrorHandler)

  def steps[T](iterator: java.util.Iterator[T]) = steps_(iterator, () => {}, defaultErrorHandler)

  def buildSteps[T >: Null](rawStepFn: => T) = new BuilderStep2[T](steps_(rawStepFn, _, _))

  def buildSteps[T](iterable: java.lang.Iterable[T]) = new BuilderStep2[T](steps_(iterable.iterator, _, _))

  def buildSteps[T](iterable: Iterator[T]) = new BuilderStep2[T](steps_(asJavaIterator(iterable), _, _))

  def buildSteps[T](iterable: Iterable[T]) = new BuilderStep2[T](steps_(asJavaIterator(iterable.iterator), _, _))

  def buildSteps[T](iterator: java.util.Iterator[T]) = new BuilderStep2[T](steps_(iterator, _, _))

  /**
   * Creates a step function from another function returning null as the indicator of the end. The final step returned
   * by the step function will contain null as the result.
   * @param rawStepFn the function returning null if there are no more steps
   * @tparam T the type of the result element
   * @return the step function built from the raw step function
   */
  private def steps_[T >: Null](rawStepFn: => T, finisher: () => Unit,
                                errorHandler: PartialFunction[Throwable, Unit]): Step[T] = {
    val fin = Finisher(finisher, errorHandler)
    def doStep(): Step[T] = rawStepFn match {
      case null => NoStep
      case line => NextStep(CloseableStepFunction(doStep, fin), line)
    }
    doStep()
  }

  private def steps_[T](iterator: java.util.Iterator[T], finisher: () => Unit,
                        errorHandler: PartialFunction[Throwable, Unit]): Step[T] = {
    val fin = Finisher(finisher, errorHandler)
    def doStep(): Step[T] = {
      if (iterator.hasNext) {
        val t = iterator.next()
        if (iterator.hasNext) {
          NextStep(CloseableStepFunction(doStep, fin), t)
        } else {
          fin.finish()
          FinalStep(t)
        }
      } else {
        NoStep
      }
    }

    try {
      doStep()
    }
    catch {
      case t: Throwable => {
        errorHandler(t)
        NoStep
      }
    }
  }

  class BuilderStep2[T](stepFactory: (() => Unit, PartialFunction[Throwable, Unit]) => Step[T]) {

    def build() = {
      stepFactory(() => {}, defaultErrorHandler)
    }

    def handleErrorsWith(errorHandler: PartialFunction[Throwable, Unit]) = new BuilderStep3[T](stepFactory, errorHandler)

    def closeWith(finisher: => Unit) = new BuilderStep4[T](stepFactory, () => finisher, defaultErrorHandler)
  }

  class BuilderStep3[T](stepFactory: (() => Unit, PartialFunction[Throwable, Unit]) => Step[T], errorHandler: PartialFunction[Throwable, Unit]) {

    def build() = {
      stepFactory(() => {}, errorHandler)
    }

    def closeWith(finisher: => Unit) = new BuilderStep4[T](stepFactory, () => finisher, errorHandler)

  }

  class BuilderStep4[T](stepFactory: (() => Unit, PartialFunction[Throwable, Unit]) => Step[T], finisher: () => Unit,
                        errorHandler: PartialFunction[Throwable, Unit]) {

    def build() = {
      stepFactory(finisher, errorHandler)
    }

  }

}
