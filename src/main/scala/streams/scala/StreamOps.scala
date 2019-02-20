package streams.scala

import java.time.{LocalDate, LocalTime}

/**
  * Refer to [[scala.collection.immutable.Stream]]
  */
class StreamOps {

  def intStream(
      from: Int,
      takeTotal: Int
  ): Stream[Int] =
    if (takeTotal < 1) Stream.Empty
    else from #:: intStream(from + 1, takeTotal - 1)

  def intMultStream(
      from: Int,
      takeTotal: Int,
      multiplier: Int => Int
  ): Stream[Int] = intStream(from, takeTotal).map(multiplier)

  def intStreamMedian(
      from: Int,
      takeTotal: Int
  ): Double = {
    val myStream = intStream(from, takeTotal)
    if (takeTotal % 2 == 0) (myStream(takeTotal / 2 - 1).toDouble + myStream(takeTotal / 2)) / 2
    else myStream(takeTotal / 2).toDouble
  }

  def datesStream(
      from: LocalDate,
      to: LocalDate
  ): Stream[LocalDate] = {
    if (from.isAfter(to)) Stream.Empty
    else from #:: datesStream(from.plusDays(1), to)
  }

  def datesStreamTimes(
      from: LocalDate,
      to: LocalDate,
      timesResolver: LocalDate => List[LocalTime]
  ): Stream[(LocalDate, List[LocalTime])] = datesStream(from, to).map(date => (date, timesResolver(date)))

  def datesStreamTimesStream(
      from: LocalDate,
      to: LocalDate,
      timesResolver: LocalDate => Stream[LocalTime]
  ): Stream[(LocalDate, LocalTime)] =
    datesStream(from, to)
      .flatMap(date => timesResolver(date).map(t => (date, t)))
}
