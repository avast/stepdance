package com.avast.steps

import scala.collection.JavaConversions._

/**
 * User: zslajchrt
 * Date: 11/25/13
 * Time: 11:34 PM
 */
object StepsBuilder {

  def steps[T >: Null](rawStepFn: => T) = steps_(rawStepFn, () => {})

  def steps[T](iterable: java.lang.Iterable[T]) = steps_(iterable.iterator, () => {})

  def steps[T](iterable: Iterator[T]) = steps_(asJavaIterator(iterable), () => {})

  def steps[T](iterable: Iterable[T]) = steps_(asJavaIterator(iterable.iterator), () => {})

  def steps[T](iterator: java.util.Iterator[T]) = steps_(iterator, () => {})

  def buildSteps[T >: Null](rawStepFn: => T) = new BuilderStep2[T](steps_(rawStepFn, _))

  def buildSteps[T](iterable: java.lang.Iterable[T]) = new BuilderStep2[T](steps_(iterable.iterator, _))

  def buildSteps[T](iterable: Iterator[T]) = new BuilderStep2[T](steps_(asJavaIterator(iterable), _))

  def buildSteps[T](iterable: Iterable[T]) = new BuilderStep2[T](steps_(asJavaIterator(iterable.iterator), _))

  def buildSteps[T](iterator: java.util.Iterator[T]) = new BuilderStep2[T](steps_(iterator, _))

  /**
   * Creates a step function from another function returning null as the indicator of the end. The final step returned
   * by the step function will contain null as the result.
   * @param rawStepFn the function returning null if there are no more steps
   * @tparam T the type of the result element
   * @return the step function built from the raw step function
   */
  private def steps_[T >: Null](rawStepFn: => T, finisher: () => Unit): Step[T] = {
    val fin = Finisher(finisher)
    def doStep(): Step[T] = rawStepFn match {
      case null => NoStep
      case line => NextStep(CloseableStepFunction(doStep, fin), line)
    }
    doStep()
  }

  private def steps_[T](iterator: java.util.Iterator[T], finisher: () => Unit): Step[T] = {
    val fin = Finisher(finisher)
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

    doStep()
  }

  class BuilderStep2[T](stepFactory: (() => Unit) => Step[T]) {

    def build() = {
      stepFactory(() => {})
    }

    def closeWith(finisher: => Unit) = new BuilderStep3[T](stepFactory, () => finisher)

  }

  class BuilderStep3[T](stepFactory: (() => Unit) => Step[T], finisher: () => Unit) {

    def build() = {
      stepFactory(finisher)
    }

  }

}
