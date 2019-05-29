package com.geowarin

import org.junit.jupiter.api.Assertions
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
  fun `begin and end`() {

    val result = IntervalPrinter().print(
      Interval("ptf", date(JANUARY, 2010), date(DECEMBER, 2011))
    )
    Assertions.assertEquals("""
    |    2010    |    2011    |
    |============|============|
   31/01         |         31/12
ptf [========================]|
    """.trimIndent(), result)
  }

  @Test
  fun `one period one group`() {

    val result = IntervalPrinter().print(
      Interval("ptf", date(DECEMBER, 2010), date(JUNE, 2011))
    )
    Assertions.assertEquals("""
    |    2010    |    2011    |
    |============|============|
    |          31/12  30/06   |
ptf |            [======]     |
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
     28/0230/06   |28/0230/06  |
ptf  [======]     |            |
ptf2              | [======]
    """.trimIndent(), result)
  }

  private fun date(month: Month, year: Int) = LocalDate.of(year, month, 1).with(lastDayOfMonth())
}