package mail

import java.io.File
import models.{JUser, JAbsence}
import org.apache.commons.mail.{DefaultAuthenticator, HtmlEmail, EmailAttachment}
import org.joda.time.DateTime
import play.api.Logger
import play.api.Play.current

/**
 * @author f.patin
 */

private trait Email {

  protected val underlying = new HtmlEmail()

  def from(fromAddress: String) = {
    underlying.setFrom(fromAddress, "", "UTF-8")
    this
  }

  def from(fromAddress: String, fromName: String) = {
    underlying.setFrom(fromAddress, fromName, "UTF-8")
    this
  }

  def addTo(address: String) = {
    underlying.addTo(address)
    this
  }

  def addTo(address: String, name: String) = {
    underlying.addTo(address, name, "UTF-8")
    this
  }

  def addCc(address: String) = {
    underlying.addCc(address, "", "UTF-8")
    this
  }

  def addCc(address: String, name: String) = {
    underlying.addCc(address, name, "UTF-8")
    this
  }

  def addBcc(address: String) = {
    underlying.addBcc(address, "", "UTF-8")
    this
  }

  def addBcc(address: String, name: String) = {
    underlying.addBcc(address, name, "UTF-8")
    this
  }

  def setSubject(subject: String) = {
    underlying.setSubject(subject)
    this
  }

  def attach(attachment: EmailAttachment) = {
    underlying.attach(attachment)
    this
  }

  def attach(xs: List[EmailAttachment]) = {
    xs.foreach(underlying.attach(_))
    this
  }

  def debug(body: (String, String)) {
    Logger.debug {
      s"""
      |From:${underlying.getFromAddress}
      |To:${underlying.getToAddresses}
      |Cc:${underlying.getCcAddresses}
      |Bcc:${underlying.getBccAddresses}
      |Subject:${underlying.getSubject}
      |HTML: ${body._1}
      |TEXT: ${body._2}
    """.stripMargin
    }
  }

  def send(body: (String, String))
}

private class RealEmail(smtpHost: String, smtpPort: Int, smtpSsl: Boolean, smtpTls: Boolean, smtpUser: Option[String], smtpPass: Option[String]) extends Email {
  def send(msg: (String, String)) {
    debug(msg)
    underlying.setHtmlMsg(msg._1)
    underlying.setTextMsg(msg._2)
    underlying.setHostName(smtpHost)
    underlying.setSmtpPort(smtpPort)
    underlying.setSSLOnConnect(smtpSsl)
    underlying.setStartTLSEnabled(smtpTls)
    for (u <- smtpUser; p <- smtpPass) yield underlying.setAuthenticator(new DefaultAuthenticator(u, p))
    underlying.setDebug(false)
    underlying.send
  }
}

private object MockEmail extends Email {
  def send(msg: (String, String)) {
    debug(msg)
  }
}

sealed trait Mailer {

  protected lazy val sender = current.configuration.getString("email.sender").getOrElse(throw new RuntimeException("email.sender needs to be set in application.conf in order to send Email"))
  protected lazy val toAbsence = current.configuration.getString("email.absence").getOrElse(throw new RuntimeException("email.sender needs to be set in application.conf in order to send Absence Email"))
  protected lazy val toCra = current.configuration.getString("email.cra").getOrElse(throw new RuntimeException("email.sender needs to be set in application.conf in order to send Activité Email"))

  protected def address(user: JUser) = s"${user.fullName} <${user.email}>"


  protected def attachment(file: File) = {
    val attachment = new EmailAttachment()
    attachment.setPath(file.getAbsolutePath)
    attachment
  }
}

object MailerAbsence extends Mailer {

  private def subject(user: JUser) = s"Demande d'absence pour ${user.fullName}"

  private def subjectCancel(user: JUser) = s"Annulation d'absence pour ${user.fullName}"

  private[MailerAbsence] object Body {

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

  def send(absence: JAbsence, file: File) = {
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

  def sendAbsences(user: JUser, file: File) = {
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
}


private case class MailerConfiguration(smtpHost: String, smtpPort: Int, smtpSsl: Boolean, smtpTls: Boolean, smtpUser: Option[String], smtpPass: Option[String])

private object MailerConfiguration {

  private lazy val mock = current.configuration.getBoolean("smtp.mock").getOrElse(false)
  private lazy val config = current.configuration.getConfig("smtp").getOrElse(throw current.configuration.reportError("smtp", "smtp needs to be set in application.conf in order to send email"))

  lazy val smtpHost = config.getString("host").getOrElse(throw current.configuration.reportError("smtp.host", "smtp.host needs to be set in application.conf in order to send email"))
  lazy val smtpPort = config.getInt("port").getOrElse(25)
  lazy val smtpSsl = config.getBoolean("ssl").getOrElse(false)
  lazy val smtpTls = config.getBoolean("tls").getOrElse(false)
  lazy val smtpUser = config.getString("user")
  lazy val smtpPassword = config.getString("password")

  def email = {
    if (mock) MockEmail
    else new RealEmail(smtpHost, smtpPort, smtpSsl, smtpTls, smtpUser, smtpPassword)
  }
}

