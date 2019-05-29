package com.geowarin

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters.*
import kotlin.streams.toList

class IntervalPrinter {
  private val ONE_MONTH = Period.ofMonths(1)
  val pattern = DateTimeFormatter.ofPattern("dd/MM")

  fun print(vararg periods: Interval): String {
    val minDate = periods.minBy { it.from }!!.from
    val maxDate = periods.maxBy { it.to }!!.to

    val begin = minDate.with(firstDayOfYear()).with(lastDayOfMonth())
    val end = maxDate.with(lastDayOfYear())

    return print(begin, end, *periods)
  }

  fun print(
    begin: LocalDate,
    end: LocalDate,
    vararg periods: Interval
  ): String {
    val maxGroupLength = periods.maxBy { it.group.length }?.group?.length ?: 0
    val indent = " ".repeat(maxGroupLength)

    val years = (begin.year..end.year)
      .joinToString("|", "|", "|") { y ->
        "    $y    "
      }.prependIndent(indent)

    val years2 = (begin.year..end.year)
      .joinToString("|", "|", "|") { "=".repeat(12) }.prependIndent(indent)

    val allMonths = begin.datesUntil(end, ONE_MONTH).toList()

    // gives the '=' position
    val toIndex: (LocalDate) -> Int = { d ->
      val nbOfYears = begin.until(d).years
      maxGroupLength + 1 + allMonths.indexOf(d) + nbOfYears
    }

    val totalNbYears = end.year - begin.year
    var dates = (0..totalNbYears).joinToString("|", "|", "|") { " ".repeat(12) }
      .prependIndent(indent)
    val allDates = periods.flatMap { listOf(it.from, it.to) }.toSortedSet()
    allDates.forEach { d ->
      val index = toIndex(d)
      dates = dates.replaceRange(index - 1, index + 2, d.format(pattern))
    }

    val groupedPeriods = periods.groupBy { it.group }
    val groups = groupedPeriods.map {
      val group = it.key.padStart(maxGroupLength)
      var str = ("$group |" + " ".repeat(allMonths.size + 1) + "|")

      val intervals = it.value
      intervals.forEach { i ->
        val b = toIndex(i.from) + 2
        val e = toIndex(i.to) + 3
        val period = i.from.until(i.to)
        val nbYears = i.to.year - i.from.year
        val len = period.months + nbYears
        str = str.replaceRange(b, e, "[" + "=".repeat(len) + "]")
      }

      str
    }

    return listOf(
      years,
      years2,
      dates,
      *groups.toTypedArray()
    ).joinToString("\n")
  }
}