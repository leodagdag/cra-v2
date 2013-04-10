package utils.time

import java.lang.Boolean

import org.joda.time.{DateTimeConstants, DateTime}

/**
 * @author f.patin
 */
object DaysOff {

  def isDayOff(date: DateTime): Boolean = {
    val dayOfYear: Int = date.getDayOfYear
    val monthOfYear: Int = date.getMonthOfYear
    val dayOfMonth: Int = date.getDayOfMonth
    val year: Int = date.getYear
    // 1er Janvier
    if (dayOfYear == 1) {
      return true
    }
    // 1er Mai
    if (monthOfYear == 5 && dayOfMonth == 1) {
      return true
    }
    // 8 Mai
    if (monthOfYear == 5 && dayOfMonth == 8) {
      return true
    }
    // 14 Juiller
    if (monthOfYear == 7 && dayOfMonth == 14) {
      return true
    }
    // 15 Août
    if (monthOfYear == 8 && dayOfMonth == 15) {
      return true
    }
    // 1er Novembre
    if (monthOfYear == 11 && dayOfMonth == 1) {
      return true
    }
    // 11 Novembre
    if (monthOfYear == 11 && dayOfMonth == 11) {
      return true
    }
    // 25 Décembre
    if (monthOfYear == 12 && dayOfMonth == 25) {
      return true
    }
    // Easter
    val easter: DateTime = getEaster(year)
    val easterMonth: Int = easter.getMonthOfYear
    val easterDay: Int = easter.getDayOfMonth
    if (easterMonth == monthOfYear && easterDay == dayOfMonth) {
      return true
    }
    // Easter Monday
    val easterMonday: DateTime = easter.plusDays(1)
    val easterMondayMonth: Int = easterMonday.getMonthOfYear
    val easterMondayDay: Int = easterMonday.getDayOfMonth
    if (easterMondayMonth == monthOfYear && easterMondayDay == dayOfMonth) {
      return true
    }
    // Ascension
    val ascension: DateTime = easter.plusDays(39)
    val ascensionMonth: Int = ascension.getMonthOfYear
    val ascensionDay: Int = ascension.getDayOfMonth
    if (ascensionMonth == monthOfYear && ascensionDay == dayOfMonth) {
      return true
    }
    // Pentecost
    val pentecost: DateTime = easter.plusDays(49)
    val pentecostMonth: Int = pentecost.getMonthOfYear
    val pentecostDay: Int = pentecost.getDayOfMonth
    if (pentecostMonth == monthOfYear && pentecostDay == dayOfMonth) {
      return true
    }
    // Pentecost Monday
    val pentecostMonday: DateTime = easter.plusDays(50)
    val pentecostMondayMonth: Int = pentecostMonday.getMonthOfYear
    val pentecostMondayDay: Int = pentecostMonday.getDayOfMonth
    if (pentecostMondayMonth == monthOfYear && pentecostMondayDay == dayOfMonth) {
      return true
    }
    false
  }

  /**
   * Return the date of Easter for a year.
   * http://fr.wikipedia.org/wiki/Calcul_de_la_date_de_P%C3%A2ques#Algorithme_de_Oudin
   */
  def getEaster(year: Integer): DateTime = {
    val g = year % 19
    val c = year / 100
    val c_4 = c / 4
    val e = (8 * c + 13) / 25
    val h = (19 * g + c - c_4 - e + 15) % 30
    val k = h / 28
    val p = 29 / (h + 1)
    val q = (21 - g) / 11
    val i = (k * p * q - 1) * k + h
    val b = year / 4 + year
    val j1 = (b + i + 2 + c_4) - c
    val j2 = j1 % 7
    val r = 28 + i - j2
    if (r > 31) new DateTime(year, 4, r - 31, 0, 0)
    else new DateTime(year, 3, r, 0, 0)
  }

  /**
   * Indicate if a date is a Saturday or a Sunday
   *
   * @param date
   * @return
   */
  def isSaturdayOrSunday(date: DateTime) = isSaturday(date) || isSunday(date)


  /**
   * Indicate if a date is a Saturday
   *
   * @param date
   * @return
   */
  def isSaturday(date: DateTime) = date.getDayOfWeek == DateTimeConstants.SATURDAY

  /**
   * Indicate if a date is a Sunday
   *
   * @param date
   * @return
   */
  def isSunday(date: DateTime) = date.getDayOfWeek == DateTimeConstants.SUNDAY
}
