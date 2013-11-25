package com.avast.steps.examples

import scala.io.Source

/**
 * User: zslajchrt
 * Date: 11/25/13
 * Time: 10:15 PM
 */
class UsingIterator extends StepDanceExample {

  def uglyIteratorAdapter() {

    class SomeRecord()

    trait SomeIteratorLike {
      def nextRecord(): SomeRecord
    }

    // UGLY iterator adapter, I must write it for every proprietary iterator
    class SomeIterator(likeIter: SomeIteratorLike)
      extends Iterator[SomeRecord] {

      var record: Option[SomeRecord] = None

      private def nextRecord() {
        record = Some(likeIter.nextRecord())
      }

      def hasNext: Boolean = {
        if (!record.isDefined) {
          nextRecord()
        }
        record.get != null
      }

      def next(): SomeRecord = try {
        if (!hasNext)
          throw new NoSuchElementException()
        record.get
      } finally {
        nextRecord()
      }
    }

    // Only now I can enjoy easy working with the iterator
  }

  def unableToCloseSources() {
    lazy val sources = List(sourceFile, sourceFile2).iterator
    val scanner: Iterator[String] =
      for (src <- sources;
           s <- Source.fromFile(src).getLines())
      yield s

    try {
      for (line <- scanner) {
        println(line)
      }
    }
    finally {
      // How to close the open files?
      // (scala-arm solves it)
    }

  }

  def decoration() {
    //*** The producer side
    val scanner: Iterator[String] = Source.fromFile(sourceFile).getLines()

    val decoratedScanner = new Iterator[String]() {
      def hasNext: Boolean = scanner.hasNext

      def next() = {
        try {
          scanner.next()
        } catch {
          case t: Throwable => {
            t.printStackTrace()
            throw t
          }
        }
      }
    }

    // Another ugly iterator again (not so ugly now)

  }

  def continuousIterationOverManyIterators() {
    /**
     * Problem:
     * In case of error we need to either skip the current iterator
     * or continue with another iterator
     * Solution: Can be solved by a decorating fail-over iterator (could be designed universal)
     */
  }

  def connectingIteratorsConditionally() {
    /**
     * Problem: We want to continue to iterate with another iterator as long as
     * 1) a certain condition occurs during the first iteration (exceeding the calculated total size)
     * 2) the last value of the first iterator ends with a certain value
     */
  }

  def lazyFolding() {
    /**
     * foldLeft/Right traverses the iterator
     * I sometimes need to aggregate values and propagate them during the iteration. They can be
     * taken into account when deciding whether to continue
     * - see Example 8 - calculating line numbers to lines
     */
  }

  def nonDeterministicRecursiveIterators() {
    // I can have an iterator with tree-traversal behavior (a tree walker)
    // See Example 19 - web crawler
  }

  def example(): Unit = {
  }
}
