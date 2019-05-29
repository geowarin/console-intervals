package com.geowarin

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters.*
import kotlin.streams.toList

val ONE_MONTH: Period = Period.ofMonths(1)
val DATE_PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")

class IntervalPrinter {

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
      .joinToString("|", "|", "|") { y -> "    $y    " }.prependIndent(indent)

    val years2 = (begin.year..end.year)
      .joinToString("|", "|", "|") { "=".repeat(12) }.prependIndent(indent)

    val allMonths = begin.datesUntil(end.plus(ONE_MONTH), ONE_MONTH).toList()

    // gives the '=' position
    val toIndex: (LocalDate) -> Int = { d ->
      val nbOfYears = begin.until(d).years
      maxGroupLength + 1 + allMonths.indexOf(d) + nbOfYears
    }

    val totalNbYears = end.year - begin.year
    var dates = (0..totalNbYears).joinToString("|", "|", "|") { " ".repeat(12) }
      .prependIndent(indent) + "   "
    val allDates = periods.flatMap { listOf(it.from, it.to) }.toSortedSet()
    allDates.forEach { d ->
      val index = toIndex(d)
      dates = dates.replaceRange(index - 2, index + 3, d.format(DATE_PATTERN))
    }
    dates = dates.trimEnd()

    val groupedPeriods = periods.groupBy { it.group }
    val groups = groupedPeriods.map { (group, intervals) ->
      val groupName = group.padStart(maxGroupLength)
      var str = (0..totalNbYears).joinToString("|", "$groupName|", "|") { " ".repeat(12) }
      intervals.forEach { i ->
        val b = toIndex(i.from)
        val e = toIndex(i.to)
        val len = e - b - 1
        str = str.replaceRange(b, e + 1, "[" + "=".repeat(len) + "]")
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