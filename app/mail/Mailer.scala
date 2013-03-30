package mail

import org.apache.commons.mail.{DefaultAuthenticator, HtmlEmail, EmailAttachment}
import models.{JUser, JAbsence}
import play.api.Logger
import play.api.Play.current
import export.PDF

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

  def debug(body: (String, Option[String])) {
    Logger.debug {
      s"""
      |From:${underlying.getFromAddress}
      |To:${underlying.getToAddresses}
      |Cc:${underlying.getCcAddresses}
      |Bcc:${underlying.getBccAddresses}
      |Subject:${underlying.getSubject}
      |HTML: ${body._1}
      |${body._2.foreach(msg => Logger.info("TEXT: " + msg))}
    """.stripMargin
    }
  }

  def send(htmlMsg: String) {
    send(htmlMsg, None)
  }

  def send(body: (String, Option[String]))
}

private class RealEmail(smtpHost: String, smtpPort: Int, smtpSsl: Boolean, smtpTls: Boolean, smtpUser: Option[String], smtpPass: Option[String]) extends Email {
  def send(msg: (String, Option[String])) {
    debug(msg)
    underlying.setHtmlMsg(msg._1)
    msg._2.foreach(underlying.setTextMsg(_))
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
  def send(msg: (String, Option[String])) {
    debug(msg)
  }
}

object Mailer {

  private lazy val sender = current.configuration.getString("email.sender").getOrElse(throw new RuntimeException("email.sender needs to be set in application.conf in order to send Email"))
  private lazy val toAbsence = current.configuration.getString("email.absence").getOrElse(throw new RuntimeException("email.sender needs to be set in application.conf in order to send Absence Email"))
  private lazy val toCra = current.configuration.getString("email.cra").getOrElse(throw new RuntimeException("email.sender needs to be set in application.conf in order to send Activité Email"))


  def apply(absence: JAbsence) {
    val user = JUser.account(absence.userId)
    val userName = s"${user.firstName.capitalize} ${user.lastName.capitalize}"
    val userAddress = s"$userName <${user.email}>"

    val manager = JUser.account(user.managerId)
    val managerAddress = s"${manager.firstName.capitalize} ${manager.lastName.capitalize} <${manager.email}>"

    val attachment = new EmailAttachment()
    attachment.setPath(PDF.absenceFile(absence, user).getAbsolutePath)

    val body = Body.absence(user)
    val subject = s"Demande d'absence pour $userName"

    val email = MailerConfiguration.email
    email.toString
    email
      .from(sender)
      .addTo(toAbsence)
      .addCc(managerAddress)
      .addCc(userAddress)
      .setSubject(subject)
      .attach(attachment)
      .send(body._1, Some(body._2))
  }

}

private case class MailerConfiguration(smtpHost: String, smtpPort: Int, smtpSsl: Boolean, smtpTls: Boolean, smtpUser: Option[String], smtpPass: Option[String]) {
}

private object MailerConfiguration {

  private lazy val mock = current.configuration.getBoolean("smtp.mock").getOrElse(false)

  lazy val email = {
    if (mock) {
      MockEmail
    } else {
      val smtpHost = current.configuration.getString("smtp.host").getOrElse(throw new RuntimeException("smtp.host needs to be set in application.conf in order to use this plugin (or set smtp.mock to true)"))
      val smtpPort = current.configuration.getInt("smtp.port").getOrElse(25)
      val smtpSsl = current.configuration.getBoolean("smtp.ssl").getOrElse(false)
      val smtpTls = current.configuration.getBoolean("smtp.tls").getOrElse(false)
      val smtpUser = current.configuration.getString("smtp.user")
      val smtpPassword = current.configuration.getString("smtp.password")
      new RealEmail(smtpHost, smtpPort, smtpSsl, smtpTls, smtpUser, smtpPassword)
    }
  }
}

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

  def absence(user: JUser): (String, String) = {
    val html = htmlAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
    val text = textAbsenceTemplate.format(s"${user.firstName.toLowerCase.capitalize} ${user.lastName.toLowerCase.capitalize}")
    (html, text)
  }
}