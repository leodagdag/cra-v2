package mail

import java.io.File
import models.{JAbsence, JUser}
import org.apache.commons.mail.EmailAttachment
import org.joda.time.DateTime

/**
 * @author f.patin
 */
object MailerAbsence extends Mailer[JAbsence] {

  private def subject(user: JUser) = s"Demande d'absence pour ${user.fullName}"

  private def subjectCancel(user: JUser) = s"Annulation d'absence pour ${user.fullName}"

  private object Body {

    val htmlAbsenceTemplate =
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

    val textAbsenceTemplate =
      """
        |Bonjour,
        |
        |Veuillez trouver ci-joint la demande d'absence de %s.
        |
        |Cordialement,
        |
        |L'application CRA
      """.stripMargin


    val htmlCancelAbsenceTemplate =
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

    val textCancelAbsenceTemplate =
      """
        |Bonjour,
        |
        |Veuillez trouver ci-joint la demande d'annulation d'absence de %s.
        |
        |Cordialement,
        |
        |L'application CRA
      """.stripMargin

    def absence(user: JUser): (String, String) = {
      val html = htmlAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
      val text = textAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
      (html, text)
    }

    def cancelAbsence(user: JUser): (String, String) = {
      val html = htmlCancelAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
      val text = textCancelAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
      (html, text)
    }
  }

  def send(absence: JAbsence, file: File): DateTime = {
    val now = DateTime.now()
    val user = JUser.account(absence.userId)
    val manager = JUser.account(user.managerId)

    val attachment = new EmailAttachment()
    attachment.setPath(file.getAbsolutePath)

    val email = MailerConfiguration.email
    email
      .from(sender)
      .addTo(toAbsence)
      .addCc(address(user))
      .addCc(address(manager))
      .setSubject(subject(user))
      .attach(attachment)
      .send(Body.absence(user))
    now
  }

  def sendAbsences(user: JUser, file: File): DateTime = {
    val now = DateTime.now()
    val manager = JUser.account(user.managerId)

    val email = MailerConfiguration.email
    email
      .from(sender)
      .addTo(toAbsence)
      .addCc(address(user))
      .addCc(address(manager))
      .setSubject(subject(user))
      .attach(attachment(file))
      .send(Body.absence(user))
    now
  }

  def sendCancelAbsence(user: JUser, file: File) = {
    val now = DateTime.now()
    val manager = JUser.account(user.managerId)

    val email = MailerConfiguration.email
    email
      .from(sender)
      .addTo(toAbsence)
      .addCc(address(user))
      .addCc(address(manager))
      .setSubject(subjectCancel(user))
      .attach(attachment(file))
      .send(Body.cancelAbsence(user))
    now
  }

  def send(absence: JAbsence, user: JUser, file: File, body: Body) = ???
}
