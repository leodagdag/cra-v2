package mail

import models.{JAbsence, JCra, JUser}
import utils.time.TimeUtils

/**
 * @author f.patin
 */

case class Body(html: String, text: String)

object AbsenceBody {
  private val htmlAbsenceTemplate =
    """
      |<p>
      |Bonjour,<br>
      |<br>
      |Veuillez trouver ci-joint la demande d'absence de <strong>%s</strong>.<br>
      |<br>
      |Cordialement,<br>
      |<br>
      |L'application CRA<br>
      |</p>
    """.stripMargin

  private val textAbsenceTemplate =
    """
      |Bonjour,
      |
      |Veuillez trouver ci-joint la demande d'absence de %s.
      |
      |Cordialement,
      |
      |L'application CRA
    """.stripMargin


  private val htmlCancelAbsenceTemplate =
    """
      |<p>
      |Bonjour,<br>
      |<br>
      |Veuillez trouver ci-joint la demande <span style="color:red"><strong>d'annulation</strong></span> d'absence de <strong>%s</strong>.<br>
      |<br>
      |Cordialement,<br>
      |<br>
      |L'application CRA<br>
      |</p>
    """.stripMargin

  private val textCancelAbsenceTemplate =
    """
      |Bonjour,
      |
      |Veuillez trouver ci-joint la demande d'annulation d'absence de %s.
      |
      |Cordialement,
      |
      |L'application CRA
    """.stripMargin


  def absence(user: JUser): Body = {
    val html = htmlAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
    val text = textAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
    Body(html, text)
  }

  def absence(abs: JAbsence, user: JUser): Body = {
    absence(user)
  }

  def cancelAbsence(user: JUser): Body = {
    val html = htmlCancelAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
    val text = textCancelAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
    Body(html, text)
  }
}

object CraBody {
  private val htmlTemplate =
    """
      |<p>
      |Bonjour,<br>
      |<br>
      |Veuillez trouver ci-joint le rapport d'activité de <strong>%s</strong> pour <strong>%s</strong>.<br>
      |<br>
      |Cordialement,<br>
      |<br>
      |L'application CRA<br>
      |</p>
    """.stripMargin

  private val textTemplate =
    """
      |Bonjour,
      |
      |Veuillez trouver ci-joint le rapport d'activité de %s pour %s.
      |
      |Cordialement,
      |
      |L'application CRA
    """.stripMargin


  def cra(cra: JCra, user: JUser): Body = {
    val craMonthYear = `MMMM yyyy`.print(TimeUtils.firstDateOfMonth(cra.year, cra.month)).capitalize
    val html = htmlTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}", craMonthYear)
    val text = textTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}", craMonthYear)
    Body(html, text)
  }

}