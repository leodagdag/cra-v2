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

  def createCancelAbsenceFile(absence: JAbsence, user: JUser): File = toFile[List[JAbsence]](user, List(absence), PDFCancelAbsence.generate, newAbsenceFile)

  def createAbsenceFile(absence: JAbsence, user: JUser): File = createAbsenceFile(List(absence), user)

  def createAbsenceFile(absences: List[JAbsence], user: JUser): File = toFile[List[JAbsence]](user, absences, PDF.getOrCreateAbsenceData, newAbsenceFile)

  private def getOrCreateAbsenceData(absences: List[JAbsence]) = {
    if (absences.head.fileId == null) createAbsenceData(absences)._2
    else DbFile.fetch(absences.head.fileId)._2
  }

  private def createAbsenceData(absences: List[JAbsence]): F.Tuple[ObjectId, Array[Byte]] = {
    val data = PDFAbsence.generate(absences)
    val fileId = DbFile.save(data)
    JAbsence.updateFileId(absences.map(_.id), fileId)
    F.Tuple(fileId, data)
  }

  // Cra
  def getEmployeeCraData(cra: JCra): Array[Byte] = PDFEmployeeCra.generate(cra)

  def getMissionCraData(cra: JCra, mission: JMission): Array[Byte] = PDFMissionCra.generate((cra, mission))

  def createProductionCraFile(cra: JCra, user: JUser): File = toFile[JCra](user, cra, createProductionCraData, newCraFile)

  private def createProductionCraData(cra: JCra) = {
    val data: Array[Byte] = PDFProductionCra.generate(cra)
    val fileId = DbFile.save(data)
    JCra.updateFileId(cra.id, fileId)
    data
  }


  private def toFile[T](user: JUser, obj: T, f: (T) => Array[Byte], newFile: (JUser, T) => File) = {
    val file = newFile(user, obj)
    FileUtils.writeByteArrayToFile(file, f(obj))
    file
  }

  private def newAbsenceFile[T](user: JUser, obj: T): File =
    new File(s"tmp/absence_${user.lastName}_${user.firstName}_${`yyyy-MM-dd_HH-mm-ss`.print(DateTime.now)}.pdf".replace(' ', '_'))

  private def newCraFile(user: JUser, cra: JCra) = {
    val date = `MMMM yyyy`.print(TimeUtils.firstDateOfMonth(cra.year, cra.month))
    new File(s"tmp/cra_${user.lastName}_${user.firstName}_${date.capitalize}.pdf".replace(' ', '_'))
  }
}
