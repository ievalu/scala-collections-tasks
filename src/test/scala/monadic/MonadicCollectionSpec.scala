package monadic

import org.scalatest._
import org.scalatest.concurrent.{Eventually, ScalaFutures}

import scala.concurrent.{ExecutionContext, Future}

class MonadicCollectionSpec extends WordSpec
  with Matchers
  with Eventually
  with ScalaFutures
  with EitherValues
  with TryValues
  with OptionValues
{

  implicit val ec: ExecutionContext = ExecutionContext.global

  val monadicCollectionOps = new MonadicCollectionOps

  "MonadicCollectionOps" should {
    "option modifier" in {
      val modifier: Int => Int = ???
      monadicCollectionOps
        .optionModifier(Option(1), modifier)
        .value shouldEqual 3

      monadicCollectionOps
        .optionModifier(Option(2), modifier)
        .value shouldEqual 6

      monadicCollectionOps
        .optionModifier(None, modifier) shouldEqual None
    }

    "option modifier option" in {
      val modifier: Int => Option[Int] = ???

      val expected = Seq(
        Some(2),
        Some(4),
        Some(6),
        Some(8),
        Some(5),
        None,
        None,
        None,
        None,
        None
      )

      (1 to 10)
        .map(Some(_))
        .map(
          monadicCollectionOps
            .optionModifierOption(_, modifier)
        ) shouldEqual expected
    }

    "try function" in {
      val function: (Int, Int) => Double = ???

      monadicCollectionOps
        .tryFunction(5, 2,  function)
        .success
        .value shouldEqual 2.5
    }

    "try function exception handling" in {
      val modifier = (int: Int) => {
        if(int < 5) {
          throw new TooSmall
        } else if(int == 5) {
          5
        } else if(int == 6) {
          throw new SixNotAllowed
        } else {
          throw new TooLarge
        }
    }

      monadicCollectionOps
        .tryModifierExceptionHandled(4, modifier)
        .success
        .value shouldEqual 0

      monadicCollectionOps
        .tryModifierExceptionHandled(5, modifier)
        .success
        .value shouldEqual 10

      val result = monadicCollectionOps
        .tryModifierExceptionHandled(6, modifier)
        .failure
      result.exception shouldBe a[RuntimeException]
      result.exception.getMessage shouldEqual "Error while applying modifier"
      result.exception.getCause shouldBe a[SixNotAllowed]

      monadicCollectionOps
        .tryModifierExceptionHandled(7, modifier)
        .failure
        .exception shouldBe a[TooLarge]
    }

    "either function exception handling" in {
      val modifier: Int => Int = (int: Int) => {
        if(int < 5) {
          throw new TooSmall
        } else if(int == 5) {
          5
        } else if(int == 6) {
          throw new SixNotAllowed
        } else {
          throw new TooLarge
        }
      }

      monadicCollectionOps
        .eitherModifierExceptionHandled(4, modifier)
        .right
        .value shouldEqual 0

      monadicCollectionOps
        .eitherModifierExceptionHandled(5, modifier)
        .right
        .value shouldEqual 10

      val result = monadicCollectionOps
        .eitherModifierExceptionHandled(6, modifier)
        .left
      result.value.reason shouldBe a[RuntimeException]
      result.value.reason.getMessage shouldEqual "Error while applying modifier"
      result.value.reason.getCause shouldBe a[SixNotAllowed]

      monadicCollectionOps
        .eitherModifierExceptionHandled(7, modifier)
        .left
        .value
        .reason shouldBe a[TooLarge]
    }

    "future calculation" in {
      val intSeqGenerator: (Int, Int) => Future[Seq[Int]] = ???
      val isMultipleOf: Int => Int => Future[Boolean] = ???

      val multiplesFuture = monadicCollectionOps
        .multiplesFuture(intSeqGenerator, isMultipleOf) _

      multiplesFuture(10, 20, 3)
        .futureValue
        .right
        .value shouldEqual Seq(12, 15, 18)

      multiplesFuture(1, 10, 11)
        .futureValue
        .left
        .value shouldEqual NoMultiplesFound

      multiplesFuture(5, 15, 0)
        .futureValue
        .left
        .value shouldBe a[ErrorDuringProcessing]
    }
  }
}
