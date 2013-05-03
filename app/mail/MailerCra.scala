package mail

import java.io.File
import models.{JUser, JCra}
import org.apache.commons.mail.EmailAttachment
import org.joda.time.DateTime
import utils.time.TimeUtils
import utils._
/**
 * @author f.patin
 */
object MailerCra extends Mailer[JCra] {

  private def subject(cra: JCra, user: JUser) = s"Rapport d'activité ${`MMMM yyyy`.print(TimeUtils.firstDateOfMonth(cra.year, cra.month)).capitalize} pour ${user.fullName}"

  def send(cra: JCra, user: JUser, file: File, body: Body) = {
    val now = DateTime.now()

    val attachment = new EmailAttachment()
    attachment.setPath(file.getAbsolutePath)

    val email = MailerConfiguration.email
    email
      .from(sender)
      .addTo(toActivite)
      .addCc(address(user))
      .setSubject(subject(cra, user))
      .attach(attachment)
      .send(body)
    now
  }
}
