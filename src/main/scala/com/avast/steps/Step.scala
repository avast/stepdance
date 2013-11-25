package com.avast.steps

import scala.annotation.tailrec
import scala.collection.JavaConversions._

object StepsBuilder {

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

  class BuilderStep2[T](stepFactory: (() => Unit) => Step[T]) {

    def build() = {
      stepFactory(() => {})
    }

    def finishWith(finisher: => Unit) = new BuilderStep3[T](stepFactory, () => finisher)

  }

  class BuilderStep3[T](stepFactory: (() => Unit) => Step[T], finisher: () => Unit) {

    def build() = {
      stepFactory(finisher)
    }

  }

}

object Step {

}

sealed trait Step[+T] {

  def map[B](f: T => B): Step[B]

  def flatMap[B](f: T => Step[B]): Step[B]

  def foreach[U](f: T => U): Unit

  def withFilter(q: T => Boolean): Step[T]

  def fold[S](initial: S)(folder: (S, T) => S): Step[S]

  def connect[S >: T](connector: (Step[T]) => Step[S]): Step[S]

  def decorate[S >: T](decorator: (() => Step[S]) => Step[S]): Step[S]

  def stopAt(stopper: T => Boolean): Step[T]
}

trait HavingResult[+T] extends Step[T] {
  val result: T
}

object NextStep {

  def apply[T](stepFn: () => Step[T], stepValue: T, finisher: => Unit = () => {}): NextStep[T] = {
    NextStep(CloseableStepFunction(stepFn, new Finisher(() => finisher)), stepValue)
  }

  @tailrec
  private[steps] def iterate[T, U](step: Step[T], f: (T) => U) {
    step match {
      case NoStep =>
      case FinalStep(res) => f(res)
      case NextStep(nfn, res) => {
        f(res)
        iterate(nfn(), f)
      }
    }
  }

  @tailrec
  private[steps] def swallow[T](nextStep: NextStep[T], condition: (T) => Boolean): Step[T] = {
    if (condition(nextStep.result)) {
      nextStep
    } else {
      nextStep.nextStepFn() match {
        case NoStep => NoStep
        case fs: FinalStep[T] => fs.withFilter(condition)
        case ns: NextStep[T] => {
          swallow(ns, condition)
        }
      }
    }
  }

  @tailrec
  private[steps] def flatMap[T, B](step: Step[T], binder: (T) => Step[B]): Step[B] = {
    step match {
      case NoStep => NoStep.flatMap(binder)
      case fs: FinalStep[T] => fs.flatMap(binder)
      case ns: NextStep[T] => {
        val NextStep(nextStepFn, result) = ns
        binder(result) match {
          case ns2: NextStep[B] => ns.decorateFlattenedStep(ns2, binder)
          case FinalStep(res) => NextStep(CloseableStepFunction(() => nextStepFn().flatMap(binder), nextStepFn.finisher), res)
          case NoStep => flatMap(nextStepFn(), binder)
        }
      }
    }
  }

  private[steps] def mappedFnGen[T, B](stepFn: CloseableStepFunction[T], mapper: (T) => B): CloseableStepFunction[B] = {
    CloseableStepFunction(() => stepFn() match {
      case NoStep => NoStep
      case FinalStep(res) => FinalStep(mapper(res))
      case NextStep(nfn, res) => NextStep(mappedFnGen(nfn, mapper), mapper(res))
    }, stepFn.finisher)
  }

  private[steps] def foldingFn[T, S](nfn: CloseableStepFunction[T], folder: (S, T) => S, nextInitial: S): CloseableStepFunction[S] = {

    val fldFn: () => Step[S] = () => nfn() match {
      case NoStep => NoStep
      case FinalStep(res) => FinalStep[S](folder(nextInitial, res))
      case NextStep(nextStepFn2, res) => {
        val foldedResult2: S = folder(nextInitial, res)
        NextStep[S](foldingFn(nextStepFn2, folder, foldedResult2), foldedResult2)
      }
    }

    CloseableStepFunction(fldFn, nfn.finisher)
  }

  private[steps] def connect[T, S >: T](nextStep: NextStep[T], connector: (Step[T]) => Step[S]): NextStep[S] = {
    val conDecorFn: () => Step[S] = () => {
      val step: Step[T] = nextStep.nextStepFn.stepFn()
      step match {
        case NoStep => {
          // we must finish the previous steps explicitly because the implicit behavior is triggered
          // for NoStep and FinalStep only while here we are possibly changing the step type
          nextStep.nextStepFn.finisher.finish()
          NoStep.connect(connector)
        }
        case fs: FinalStep[T] => {
          nextStep.nextStepFn.finisher.finish()
          fs.connect(connector)
        }
        case ns: NextStep[T] => connect(ns, connector)
      }
    }

    NextStep[S](CloseableStepFunction[S](conDecorFn, nextStep.nextStepFn.finisher), nextStep.result)
  }

}

object Finisher extends Finisher(() => {})

case class Finisher(finisherFn: () => Unit) {

  private[this] var called = false

  //  def handleError(t: Throwable) {
  //    finish()
  //  }

  def finish(): Unit = if (!called) {
    try {
      finisherFn()
    }
    finally {
      called = true
    }
  }
}

case class CloseableStepFunction[T](stepFn: () => Step[T], finisher: Finisher) extends (() => Step[T]) {
  override def apply(): Step[T] = {
    val step: Step[T] = try {
      stepFn()
    }
    catch {
      case t: Throwable => {
        try {
          //finisher.handleError(t)
          finisher.finish()
        }
        catch {
          case tt: Throwable => tt.printStackTrace()
        }
        throw t
      }
    }
    step match {
      case NoStep => {
        finisher.finish()
      }
      case FinalStep(_) => {
        finisher.finish()
      }
      case _ =>
    }
    step
  }
}

case class NextStep[T](nextStepFn: CloseableStepFunction[T], result: T) extends Step[T] with HavingResult[T] {

  def map[B](mapper: (T) => B): Step[B] = {
    NextStep(NextStep.mappedFnGen(nextStepFn, mapper), mapper(result))
  }

  def flatMap[B](binder: (T) => Step[B]): Step[B] = {
    NextStep.flatMap(this, binder)
  }

  private[steps] def decorateFlattenedStep[B](decorStep: NextStep[B], binder: (T) => Step[B]): NextStep[B] = {
    val decorStepFn: () => Step[B] = () => {
      val dec: NextStep[B] = decorStep
      val nfnCl: CloseableStepFunction[B] = dec.nextStepFn
      val nfn: () => Step[B] = nfnCl
      val decorNextStep: Step[B] = nfn()

      decorNextStep match {
        case ns: NextStep[B] => decorateFlattenedStep(ns, binder)
        case FinalStep(res) => NextStep[B](CloseableStepFunction(() => nextStepFn().flatMap(binder), nextStepFn.finisher), res)
        case NoStep => nextStepFn().flatMap(binder)
      }
    }

    NextStep(CloseableStepFunction(decorStepFn, decorStep.nextStepFn.finisher), decorStep.result)
  }

  def foreach[U](f: (T) => U): Unit = {
    NextStep.iterate(this, f)
  }

  def withFilter(condition: (T) => Boolean): Step[T] = {
    NextStep.swallow(this, condition) match {
      case NoStep => NoStep
      case fs: FinalStep[T] => fs
      case NextStep(nfn, res) => {
        NextStep(CloseableStepFunction(() => {
          nfn().withFilter(condition)
        }, nfn.finisher), res)
      }
    }
  }

  def fold[S](initial: S)(folder: (S, T) => S): Step[S] = {
    val foldedResult = folder(initial, result)
    NextStep[S](NextStep.foldingFn(nextStepFn, folder, foldedResult), foldedResult)
  }

  def connect[S >: T](connector: (Step[T]) => Step[S]): Step[S] = NextStep.connect(this, connector)

  def decorate[S >: T](decorator: (() => Step[S]) => Step[S]): Step[S] = {
    NextStep[S](CloseableStepFunction[S](() => {
      decorator(nextStepFn).decorate(decorator)
    }, nextStepFn.finisher), result)
  }

  def stopAt(stopper: T => Boolean): Step[T] = {
    if (stopper(result)) {
      FinalStep(result)
    } else {
      NextStep(CloseableStepFunction(() => {
        nextStepFn().stopAt(stopper)
      }, nextStepFn.finisher), result)
    }
  }
}

case class FinalStep[+T](result: T) extends Step[T] with HavingResult[T] {
  def map[B](mapper: (T) => B): Step[B] = FinalStep(mapper(result))

  def flatMap[B](f: (T) => Step[B]): Step[B] = f(result)

  def foreach[U](f: (T) => U): Unit = {
    f(result)
  }

  def withFilter(q: (T) => Boolean): Step[T] = if (q(result))
    this
  else
    NoStep

  def fold[S](initial: S)(folder: (S, T) => S): Step[S] = FinalStep(folder(initial, result))

  def connect[S >: T](connector: (Step[T]) => Step[S]): Step[S] = {
    connector(this) match {
      case NoStep => NoStep
      case fs: FinalStep[S] => NextStep(CloseableStepFunction(() => fs, Finisher), result)
      case ns: NextStep[S] => NextStep(CloseableStepFunction(() => ns, ns.nextStepFn.finisher), result)
    }
  }

  def decorate[S >: T](decorator: (() => Step[S]) => Step[S]): Step[S] = this

  def stopAt(stopper: T => Boolean): Step[T] = this
}

case object NoStep extends Step[Nothing] {

  def map[B](f: (Nothing) => B) = this

  def flatMap[B](f: (Nothing) => Step[B]) = this

  def foreach[U](f: (Nothing) => U): Unit = ()

  def withFilter(q: (Nothing) => Boolean) = this

  def fold(folder: (Nothing, Nothing) => Nothing): Step[Nothing] = this

  def fold[S](initial: S)(folder: (S, Nothing) => S): Step[S] = this

  def connect[S >: Nothing](connector: (Step[Nothing]) => Step[S]): Step[S] = connector(this)

  def decorate[S >: Nothing](decorator: (() => Step[S]) => Step[S]): Step[S] = this

  def stopAt(stopper: Nothing => Boolean): Step[Nothing] = this
}
