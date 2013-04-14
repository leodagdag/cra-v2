package mail

import java.io.File
import models.JUser
import org.apache.commons.mail.{DefaultAuthenticator, HtmlEmail, EmailAttachment}
import org.joda.time.DateTime
import play.api.Logger
import play.api.Play.current

/**
 * @author f.patin
 */

trait Email {

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

  def debug(body: Body) {
    debug((body.html, body.text))
  }

  def send(body: (String, String))

  def send(body: Body)

}

class RealEmail(smtpHost: String, smtpPort: Int, smtpSsl: Boolean, smtpTls: Boolean, smtpUser: Option[String], smtpPass: Option[String]) extends Email {
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

  def send(body: Body) {
    debug(body)
    underlying.setHtmlMsg(body.html)
    underlying.setTextMsg(body.text)
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

  def send(body: Body) {
    send((body.html, body.text))
  }
}

trait Mailer[T] {

  protected lazy val sender = current.configuration.getString("email.sender").getOrElse(throw new RuntimeException("email.sender needs to be set in application.conf in order to send Email"))
  protected lazy val toAbsence = current.configuration.getString("email.absence").getOrElse(throw new RuntimeException("email.sender needs to be set in application.conf in order to send Absence Email"))
  protected lazy val toActivite = current.configuration.getString("email.cra").getOrElse(throw new RuntimeException("email.sender needs to be set in application.conf in order to send Activité Email"))

  protected def address(user: JUser) = s"${user.fullName} <${user.email}>"


  protected def attachment(file: File): EmailAttachment = {
    val attachment = new EmailAttachment()
    attachment.setPath(file.getAbsolutePath)
    attachment
  }

  def send(obj: T, user: JUser, file: File, body: Body): DateTime
}


case class MailerConfiguration(smtpHost: String, smtpPort: Int, smtpSsl: Boolean, smtpTls: Boolean, smtpUser: Option[String], smtpPass: Option[String])

object MailerConfiguration {

  private lazy val mock = current.configuration.getBoolean("smtp.mock").getOrElse(false)
  private lazy val config = current.configuration.getConfig("smtp").getOrElse(throw current.configuration.reportError("smtp", "smtp needs to be set in application.conf in order to send email"))

  private lazy val smtpHost = config.getString("host").getOrElse(throw current.configuration.reportError("smtp.host", "smtp.host needs to be set in application.conf in order to send email"))
  private lazy val smtpPort = config.getInt("port").getOrElse(25)
  private lazy val smtpSsl = config.getBoolean("ssl").getOrElse(false)
  private lazy val smtpTls = config.getBoolean("tls").getOrElse(false)
  private lazy val smtpUser = config.getString("user")
  private lazy val smtpPassword = config.getString("password")

  def email = {
    if (mock) MockEmail
    else new RealEmail(smtpHost, smtpPort, smtpSsl, smtpTls, smtpUser, smtpPassword)
  }
}

