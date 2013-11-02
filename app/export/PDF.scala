package export

import java.io.File
import models._
import org.apache.commons.io.FileUtils
import org.bson.types.ObjectId
import org.joda.time.DateTime
import play.libs.F
import scala.collection.convert.WrapAsJava._
import scala.collection.convert.WrapAsScala._
import utils._
import utils.time.TimeUtils

/**
 * @author f.patin
 */
object PDF {

  // Absence

  def getAbsenceData(absence: JAbsence) = DbFile.fetch(absence.fileId)

  def createAbsenceData(jAbsences: java.util.List[JAbsence]): F.Tuple[ObjectId, Array[Byte]] = createAbsenceData(jAbsences.toList)

  def createCancelAbsenceFile(absence: JAbsence, user: JUser): File = toFile[List[JAbsence]](user, List(absence), PDFCancelAbsence.generate, newCancelAbsenceFile)

  def createAbsenceFile(absence: JAbsence, user: JUser): File = createAbsenceFile(List(absence), user)

  def createAbsenceFile(absences: List[JAbsence], user: JUser): File = toFile[List[JAbsence]](user, absences, getOrCreateAbsenceData, newAskAbsenceFile)

  private def getOrCreateAbsenceData(absences: List[JAbsence]): Array[Byte] = {
    if (absences.head.fileId == null) createAbsenceData(absences)._2
    else DbFile.fetch(absences.head.fileId)._2
  }

  private def createAbsenceData(absences: List[JAbsence]): F.Tuple[ObjectId, Array[Byte]] = {
    val data = PDFAbsence.generate(absences)
    val fileId = DbFile.save(data, absenceTitle(JUser.identity(absences.head.userId)))
    JAbsence.updateFileId(absences.map(_.id), fileId)
    F.Tuple(fileId, data)
  }

  // Cra
  def getEmployeeCraData(cra: JCra): Array[Byte] = PDFEmployeeCra.generate(cra)

  def getMissionCraData(cra: JCra, mission: JMission): Array[Byte] = PDFMissionCra.generate((cra, mission))

  def createProductionCraFile(cra: JCra, user: JUser): File = toFile[JCra](user, cra, createProductionCraData, newCraFile)

  private def createProductionCraData(cra: JCra) = {
    val data: Array[Byte] = PDFProductionCra.generate(cra)
    val fileId = DbFile.save(data, craTitle(JUser.identity(cra.userId), cra))
    JCra.updateFileId(cra.id, fileId)
    data
  }


  private def toFile[T](user: JUser, obj: T, f: (T) => Array[Byte], newFile: (JUser, T) => File) = {
    val file = newFile(user, obj)
    FileUtils.writeByteArrayToFile(file, f(obj))
    file
  }

  private def newCancelAbsenceFile[T](user: JUser, obj: T): File = new File(s"tmp/${absenceTitle(user, false)}")

  private def newAskAbsenceFile[T](user: JUser, obj: T): File = new File(s"tmp/${absenceTitle(user)}")

  private def newCraFile(user: JUser, cra: JCra) = new File(s"tmp/${craTitle(user, cra)}")

  def absenceTitle(user: JUser, create: Boolean = true): String = {
    val date = `yyyy-MM-dd_HH-mm-ss`.print(DateTime.now)
    s"${if(create){"Demande"}else{"Annulation"}}_Absence_${user.fullName()}_$date.pdf".replace(' ', '_')
  }

  private def craTitle(user: JUser, cra: JCra): String = {
    val date = `MMMM yyyy`.print(TimeUtils.firstDateOfMonth(cra.year, cra.month))
    s"CRA_${user.fullName()}_$date.pdf".replace(' ', '_')
  }
}
