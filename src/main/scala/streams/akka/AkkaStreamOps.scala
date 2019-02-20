package streams.akka

import java.time.{LocalDate, LocalTime}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import scala.concurrent.{ExecutionContext, Future}

/**
  * Refer to [[https://doc.akka.io/docs/akka/2.5/stream/]]
  */
class AkkaStreamOps(implicit ac: ActorSystem, mat: Materializer, ec: ExecutionContext) {

  def intStream(
      from: Int,
      takeTotal: Int
  ): Source[Int, NotUsed] = Source(from until (from + takeTotal * 2) by 2)

  def datesStreamTimesStream(
      from: LocalDate,
      to: LocalDate,
      timesResolver: LocalDate => Source[LocalTime, NotUsed]
  ): Source[(LocalDate, LocalTime), NotUsed] = {
    def datesList(
       from: LocalDate,
       to: LocalDate): List[LocalDate] = {
      if (from.isAfter(to)) Nil
      else from :: datesList(from.plusDays(1), to)
    }
      val dates:Source[LocalDate, NotUsed] =
        Source(datesList(from, to))
      dates.flatMapConcat(date => timesResolver(date).map(t => (date, t)))
  }

  def intStreamFutureModifier(
      from: Int,
      to: Int,
      modifier: Int => Future[Int]
  ): Source[Int, NotUsed] =  Source(from to to).mapAsync(1)(x => modifier(x))
  //Source(Await.result(Future.sequence((from to to).map(x => modifier(x))), Duration("1 second")))

  def intStreamExceptionHandled(
      from: Int,
      to: Int,
      modifier: Int => Int
  ): Source[Either[RuntimeException, Int], NotUsed] = {
    Source(from to to).map(x =>
      try {
        Right(modifier(x))
      } catch {
        case expected: BadButExpectedException => Left(expected)
        case unexpected: RuntimeException => Left(new RuntimeException("Error while applying modifier", unexpected))
      })
  }
}
