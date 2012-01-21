package de.lmu.ifi.dbs.knowing.test

import weka.core.{ Attribute, Instances, Instance }
import org.scalatest.matchers._
import org.scalatest._

trait InstancesMatchers extends ShouldMatchers {

  //TODO this is not very handsome
  val empty = new InstancesEmptyMatcher

  class InstancesEmptyMatcher extends Matcher[Instances] {
    def apply(left: Instances): MatchResult = MatchResult(left.isEmpty, "Instances are not empty", "Instances are empty")
  }

  //TODO this doesn't work as Instances inherites from List
  def attribute(expectedValue: String) =
    new HavePropertyMatcher[Instances, String] {
      def apply(inst: Instances) =
        HavePropertyMatchResult(
          inst.attribute(expectedValue) != null,
          "attributes",
          expectedValue,
          "Attribute not found")
    }
  
  //TODO doesn't seem to work this way either
  /*
  extends Matchers
  def attribute(expectedValue: String) = {
    new ResultOfHaveWordForJavaList(left, shouldBeTrue) {

    }
  }*/

  
  //Copied from ShouldMatchers and Matchers
  private object ShouldMethodHelper {
    def shouldMatcher[T](left: T, rightMatcher: Matcher[T]) {
      rightMatcher(left) match {
        case MatchResult(false, failureMessage, _, _, _) => throw newOverridenTestFailedException(failureMessage)
        case _ => ()
      }
    }
  }

  private def newOverridenTestFailedException(message: String): Throwable = {
    val fileNames = List("Matchers.scala", "ShouldMatchers.scala", "MustMatchers.scala")
    val temp = new RuntimeException
    val stackDepth = temp.getStackTrace.takeWhile(stackTraceElement => fileNames.exists(_ == stackTraceElement.getFileName) || stackTraceElement.getMethodName == "newTestFailedException").length
    // if (stackDepth != 4) throw new OutOfMemoryError("stackDepth in Matchers.scala is: " + stackDepth)
    new TestFailedException(message, stackDepth)
  }

  //TODO This wrapper doesn't seem to to work :(
  final class InstancesShouldWrapper(left: Instances) {

    /**
     * This method enables syntax such as the following:
     *
     * <pre class="stHighlight">
     * javaList should equal (someOtherJavaList)
     *          ^
     * </pre>
     */
    def should(rightMatcher: Matcher[Instances]) {
      ShouldMethodHelper.shouldMatcher(left, rightMatcher)
    }

    /**
     * This method enables syntax such as the following:
     *
     * <pre class="stHighlight">
     * javaList should have length (3)
     *          ^
     * </pre>
     */
    def should(haveWord: HaveWord): ResultOfHaveWordForInstances = {
      new ResultOfHaveWordForInstances(left, true)
    }

    /**
     * This method enables syntax such as the following:
     *
     * <pre class="stHighlight">
     * javaList should not have length (3)
     *          ^
     * </pre>
     */
    //    def should(notWord: NotWord): ResultOfNotWordForJavaList[Instance, Instances] = {
    //      new ResultOfNotWordForJavaList(left, false)
    //    }

    //Implicit conversion. Is the convertToJavaListShouldWrapper stronger?
    implicit def convertToInstancesShouldWrapper[T](o: Instances): InstancesShouldWrapper = new InstancesShouldWrapper(o)
  }

  //This does the actual matching
  final class ResultOfHaveWordForInstances(left: Instances, shouldBeTrue: Boolean) {

    def attribute(name: String) = {
      left.attribute(name)

      /*      throw newOverridenTestFailedException(
        FailureMessages(
          if (shouldBeTrue) "didNotHaveExpectedLength" else "hadExpectedLength",
          left,
          expectedLength))*/
    }

  }
}

object InstancesMatchers extends InstancesMatchers