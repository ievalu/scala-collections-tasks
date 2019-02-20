package streams.scala

import java.time.{DayOfWeek, LocalDate, LocalTime}

import org.scalatest.{Matchers, WordSpec}

class StreamOpsSpec extends WordSpec with Matchers {

  val streamOps = new StreamOps

  "StreamOps" should {
    "valid int stream" in {
      streamOps
        .intStream(3, 2)
        .toList shouldEqual List(3, 4)

      streamOps
        .intStream(5, 5)
        .toList shouldEqual List(5, 6, 7, 8, 9)
    }

    "valid int mult stream" in {
      streamOps
        .intMultStream(3, 2, (el: Int) => el * 2)
        .toList shouldEqual List(6, 8)
    }

    "valid median of int stream" in {
      streamOps
        .intStreamMedian(5, 2) shouldEqual 5.5

      streamOps
      .intStreamMedian(5, 5) shouldEqual 7.0

      streamOps
        .intStreamMedian(1, 100) shouldEqual 50.5
    }

    "valid dates stream" in {
      val expected = List(
        LocalDate.of(2018, 1, 1),
        LocalDate.of(2018, 1, 2),
        LocalDate.of(2018, 1, 3),
        LocalDate.of(2018, 1, 4),
        LocalDate.of(2018, 1, 5)
      )
      streamOps
        .datesStream(
          LocalDate.of(2018, 1, 1),
          LocalDate.of(2018, 1, 5)
        )
        .toList shouldEqual expected
    }

    "valid dates stream times" in {
      /**
        * Should pick 9:30 and 10:00 times for weekdays and 7:00 to weekends
        */
      val timesResolver: LocalDate => List[LocalTime] = {
        date =>
          if (date.getDayOfWeek == DayOfWeek.SATURDAY || date.getDayOfWeek == DayOfWeek.SUNDAY)
            List(LocalTime.of(7, 0))
          else
            List(
              LocalTime.of(9, 30),
              LocalTime.of(10, 0)
            )
      }

      {
        val expected = List(
          (
            LocalDate.of(2018, 1, 5),
            List(
              LocalTime.of(9, 30),
              LocalTime.of(10, 0)
            )
          ),
          (
            LocalDate.of(2018, 1, 6),
            List(LocalTime.of(7, 0))
          )
        )

        streamOps
          .datesStreamTimes(
            LocalDate.of(2018, 1, 5),
            LocalDate.of(2018, 1, 6),
            timesResolver
          )
          .toList shouldEqual expected
      }
    }
  }

  "valid dates stream times stream" in {
    /**
      * Should pick 9:30 and 10:00 times for weekdays and 7:00 to weekends
      */
    val timesResolver: LocalDate => Stream[LocalTime] = {
      date =>
        if (date.getDayOfWeek == DayOfWeek.SATURDAY || date.getDayOfWeek == DayOfWeek.SUNDAY)
          Stream(LocalTime.of(7, 0))
        else
          Stream(
            LocalTime.of(9, 30),
            LocalTime.of(10, 0)
          )
    }

    {
      val expected = List(
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

      streamOps
        .datesStreamTimesStream(
          LocalDate.of(2018, 1, 5),
          LocalDate.of(2018, 1, 6),
          timesResolver
        )
        .toList shouldEqual expected
    }
  }
}
