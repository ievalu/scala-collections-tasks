package monadic

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class MonadicCollectionOps(implicit ec: ExecutionContext) {

  def optionModifier(
      value: Option[Int],
      modifier: Int => Int
  ): Option[Int] = value match {
    case Some(x) => Some(modifier(x))
    case None => None
  }

  def optionModifierOption(
      value: Option[Int],
      modifier: Int => Option[Int]
  ): Option[Int] = value match {
    case None => None
    case Some(x) => modifier(x)
  }

  def tryFunction(
      value: Int,
      divisor: Int,
      function: (Int, Int) => Double
  ): Try[Double] = Try(function(value, divisor))

  def tryModifierExceptionHandled(
      value: Int,
      modifier: Int => Int
  ): Try[Double] = {
    def inner(ex: Throwable):Double = { throw new RuntimeException("Error while applying modifier", ex)}
    Try {
      modifier(value).toDouble
    } match {
      case Failure(t) => t match {
        case _: TooSmall => Try(0.0)
        case ex: SixNotAllowed => Try(inner(ex))
        case _ => Try(modifier(value).toDouble)
      }
      case Success(_) => Try(modifier(value).toDouble * 2)
    }
  }

  def eitherModifierExceptionHandled(
      value: Int,
      modifier: Int => Int
  ): Either[ModifierFailure, Double] = {
    def inner(ex: Throwable):Either[ModifierFailure, Double] =
      Left(ModifierFailure(new RuntimeException("Error while applying modifier", ex)))
    try{
      Right(modifier(value) * 2)
    } catch {
      case _: TooSmall => Right(0.0)
      case ex: SixNotAllowed => inner(ex)
      case ex: TooLarge => Left(ModifierFailure(ex))
    }
  }

  def multiplesFuture(
     intSeqGenerator: (Int, Int) => Future[Seq[Int]],
     isMultipleOf: Int => Int => Future[Boolean]
   )(
     from: Int,
     to: Int,
     multipleOf: Int
   ): Future[Either[MultiplesSearchFailure, Seq[Int]]] = {
    val result = for {
      ints <- intSeqGenerator(from, to)
      maybeMultiples <- Future.sequence(
        ints
          .map(int => isMultipleOf(multipleOf)(int)
            .recover({
              case _: ArithmeticException => true
            })
            .map(isMultiple => (int, isMultiple)))
      )
      multiplePairs <- {
        if (maybeMultiples.filter(t => t._2).isEmpty)
          Future(Left(NoMultiplesFound))
        else if (maybeMultiples.filter(t => t._2).length == maybeMultiples.length && multipleOf == 0)
          Future(Left(ErrorDuringProcessing(new ArithmeticException)))
        else Future(Right(maybeMultiples.filter(t => t._2).map(t => t._1))) }
    } yield multiplePairs
    result
  }
}
