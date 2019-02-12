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
  ): Source[Int, NotUsed] = ???

  def datesStreamTimesStream(
      from: LocalDate,
      to: LocalDate,
      timesResolver: LocalDate => Source[LocalTime, NotUsed]
  ): Source[(LocalDate, LocalTime), NotUsed] = ???

  def intStreamFutureModifier(
      from: Int,
      to: Int,
      modifier: Int => Future[Int]
  ): Source[Int, NotUsed] = ???

  def intStreamExceptionHandled(
      from: Int,
      to: Int,
      modifier: Int => Int
  ): Source[Either[RuntimeException, Int], NotUsed] = ???
}
