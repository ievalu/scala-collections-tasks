package streams.akka

import java.time.{LocalDate, LocalTime}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, Materializer}
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.{EitherValues, Matchers, WordSpec}

import scala.concurrent.{ExecutionContext, Future}

class AkkaStreamOpsSpec extends WordSpec
  with Matchers
  with Eventually
  with ScalaFutures
  with EitherValues
{

  implicit val ac: ActorSystem = ActorSystem()
  implicit val mat: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = ExecutionContext.global

  val akkaStreamOps = new AkkaStreamOps

  "AkkaStreamOps" should {
    "valid int stream" in {
      akkaStreamOps
        .intStream(5, 3)
        .runWith(Sink.seq).futureValue shouldEqual Seq(5, 7, 9)
    }

    "valid date stream times stream" in {
      val timesResolver: LocalDate => Source[LocalTime, NotUsed] = ???

      {
        val expected = Seq(
          (
            LocalDate.of(2018, 1, 5),
            LocalTime.of(9, 30)
          ),
          (
            LocalDate.of(2018, 1, 5),
            LocalTime.of(10, 0)
          ),
          (
            LocalDate.of(2018, 1, 6),
            LocalTime.of(7, 0)
          )
        )

        akkaStreamOps
          .datesStreamTimesStream(
            LocalDate.of(2018, 1, 5),
            LocalDate.of(2018, 1, 6),
            timesResolver
          )
          .runWith(Sink.seq).futureValue shouldEqual expected
      }
    }

    "valid int stream future modifier" in {
      val modifier: Int => Future[Int] = ???
      val expected = Seq(9, 12, 15, 18)
      akkaStreamOps
        .intStreamFutureModifier(3, 6, modifier)
        .runWith(Sink.seq).futureValue shouldEqual expected
    }

    "valid int stream exception handling" in {
      val modifier: Int => Nothing = (_: Int) => throw new BadButExpectedException
      val result = akkaStreamOps
        .intStreamExceptionHandled(1, 3, modifier)
        .runWith(Sink.seq).futureValue.head
      result.left.value shouldBe a[BadButExpectedException]
    }

    "valid int stream unexpected exception handling" in {
      val modifier: Int => Nothing = (_: Int) => throw new RuntimeException
      val result = akkaStreamOps
        .intStreamExceptionHandled(1, 3, modifier)
        .runWith(Sink.seq).futureValue.head
      result.left.value shouldBe a[RuntimeException]
      result.left.value.getMessage shouldEqual "Error while applying modifier"
      result.left.value.getCause should not equal null
    }
  }
}
