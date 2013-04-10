package controllers

import export.PDF
import mail.MailerAbsence
import models.{JUser, JAbsence}
import org.joda.time.DateTime
import play.api.Logger
import play.api.mvc.Action
import scala.collection.JavaConverters._
import security.MyDeadboltHandler

/**
 * @author f.patin
 */
object Batches extends BaseController {

  def absence = Restrict(batch, new MyDeadboltHandler()) {
    Action {
      Logger.info(s"Start Batch absence @ ${DateTime.now().toString("dd/MM/yyyy @ HH:mm:ss")}")
      val userIds = JAbsence.usersToSend.asScala.toList
      userIds.foreach {
        userId =>
          JAbsence.byUserToSent(userId).asScala.toList
            .groupBy(a => (a.startDate.getYear, a.startDate.getMonthOfYear))
            .foreach {
            abs =>
              val user = JUser.account(userId)
              val file = PDF.getOrCreateAbsenceFile(abs._2, user)
              val sentDate = MailerAbsence.sendAbsences(user, file)
              JAbsence.updateSentDate(abs._2.map(_.id).asJava, sentDate)
          }
      }
      Logger.info(s"End Batch absence @ ${DateTime.now().toString("dd/MM/yyyy @ HH:mm:ss")}")
      Ok("Batch finished")
    }
  }

}
