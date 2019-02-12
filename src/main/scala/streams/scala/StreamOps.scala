package streams.scala

import java.time.{LocalDate, LocalTime}

/**
  * Refer to [[scala.collection.immutable.Stream]]
  */
class StreamOps {

  def intStream(
      from: Int,
      takeTotal: Int
  ): Stream[Int] = ???

  def intMultStream(
      from: Int,
      takeTotal: Int,
      multiplier: Int => Int
  ): Stream[Int] = ???

  def intStreamMedian(
      from: Int,
      takeTotal: Int
  ): Double = ???

  def datesStream(
      from: LocalDate,
      to: LocalDate
  ): Stream[LocalDate] = ???

  def datesStreamTimes(
      from: LocalDate,
      to: LocalDate,
      timesResolver: LocalDate => List[LocalTime]
  ): Stream[(LocalDate, List[LocalTime])] = ???

  def datesStreamTimesStream(
      from: LocalDate,
      to: LocalDate,
      timesResolver: LocalDate => Stream[LocalTime]
  ): Stream[(LocalDate, LocalTime)] = ???
}
