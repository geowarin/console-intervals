package com.geowarin

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month
import java.time.Month.*
import java.time.temporal.TemporalAdjusters.lastDayOfMonth

class IntervalPrinterTest {

  @Test
  fun `empty`() {

    val result = IntervalPrinter().print(
      date(JANUARY, 2010),
      date(DECEMBER, 2011)
    )
    Assertions.assertEquals("""
    |    2010    |    2011    |
    |============|============|
    |            |            |
    """.trimIndent(), result)
  }

  @Test
  internal fun `test replace range`() {
    val res = (1..9).joinToString("")
      .replaceRange(0, 5, "-".repeat(5))

    Assertions.assertEquals("-----6789", res)

    val res2 = (1..9).joinToString("")
      .replaceRange(3, 5, "-".repeat(2))

    Assertions.assertEquals("123--6789", res2)
  }

  @Test
  fun `begin and end`() {

    val result = IntervalPrinter().print(
      Interval("ptf", date(JANUARY, 2010), date(DECEMBER, 2011))
    )
    Assertions.assertEquals("""
   |    2010    |    2011    |
   |============|============|
  31/01         |         31/12
ptf|[=======================]|
    """.trimIndent(), result)
  }

  @Test
  fun `one group one period `() {

    val result = IntervalPrinter().print(
      Interval("ptf", date(DECEMBER, 2010), date(JUNE, 2011))
    )
    Assertions.assertEquals("""
   |    2010    |    2011    |
   |============|============|
   |         31/12  30/06    |
ptf|           [======]      |
    """.trimIndent(), result)
  }

  @Test
  fun `one group two periods`() {

    val result = IntervalPrinter().print(
      Interval("ptf", date(MARCH, 2010), date(JANUARY, 2011)),
      Interval("ptf", date(MARCH, 2012), date(AUGUST, 2012))
    )
    Assertions.assertEquals("""
   |    2010    |    2011    |    2012    |
   |============|============|============|
   |31/03      31/01         |31/0331/08  |
ptf|  [==========]           |  [====]    |
    """.trimIndent(), result)
  }

  @Test
  fun `two groups`() {

    val result = IntervalPrinter().print(
      Interval("ptf", date(FEBRUARY, 2010), date(JUNE, 2010)),
      Interval("ptf 2", date(FEBRUARY, 2011), date(JUNE, 2011))
    )
    Assertions.assertEquals("""
     |    2010    |    2011    |
     |============|============|
     28/030/06    28/030/06    |
  ptf| [===]      |            |
ptf 2|            | [===]      |
    """.trimIndent(), result)
  }

  private fun date(month: Month, year: Int) = LocalDate.of(year, month, 1).with(lastDayOfMonth())
}