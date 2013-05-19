package utils.uploader

import java.util.Locale
import models.JUser


/**
 * format : "NOM","PRENOM","MAIL","TRIGRAMME","MANAGER"
 * @author f.patin
 */
object UserUploader extends Uploader[JUser] {


  private def extractUsername(line: Array[String]) = line(2).substring(0, line(2).indexOf("@"))

  def exist: (Array[String]) => Boolean = {
    line: Array[String] =>
      JUser.exist(extractUsername(line))
  }

  /**
   * format : "NOM","PRENOM","MAIL","TRIGRAMME","MANAGER"
   * @return imported JUser
   */
  def importOneLine: (Array[String]) => JUser = {
    line =>
      val user = new JUser()
      val username = extractUsername(line)
      user.username = username
      user.lastName = line(0).toLowerCase(Locale.FRANCE)
      user.firstName = line(1).toLowerCase(Locale.FRANCE)
      user.email = line(2)
      user.trigramme = line(3).toUpperCase(Locale.FRANCE)
      user.isManager = line(4).toBoolean
      JUser.create(user)
  }
}
