package monadic

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class MonadicCollectionOps(implicit ec: ExecutionContext) {

  def optionModifier(
      value: Option[Int],
      modifier: Int => Int
  ): Option[Int] = ???

  def optionModifierOption(
      value: Option[Int],
      modifier: Int => Option[Int]
  ): Option[Int] = ???

  def tryFunction(
      value: Int,
      divisor: Int,
      function: (Int, Int) => Double
  ): Try[Double] = ???

  def tryModifierExceptionHandled(
      value: Int,
      modifier: Int => Int
  ): Try[Double] = ???

  def eitherModifierExceptionHandled(
      value: Int,
      modifier: Int => Int
  ): Either[ModifierFailure, Double] = ???

  def multiplesFuture(
      intSeqGenerator: (Int, Int) => Future[Seq[Int]],
      isMultipleOf: Int => Int => Future[Boolean]
  )(
      from: Int,
      to: Int,
      multipleOf: Int
  ): Future[Either[MultiplesSearchFailure, Seq[Int]]] = ???
}
