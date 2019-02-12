package object monadic {

  class SixNotAllowed extends RuntimeException
  class TooSmall extends RuntimeException
  class TooLarge extends RuntimeException

  case class ModifierFailure(reason: Throwable)

  trait MultiplesSearchFailure
  case object NoMultiplesFound extends MultiplesSearchFailure
  case class ErrorDuringProcessing(cause: Throwable) extends MultiplesSearchFailure

}
