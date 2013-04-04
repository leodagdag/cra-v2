package export

import java.io.File
import java.util.UUID
import models.{JCra, JUser, DbFile, JAbsence}
import org.apache.commons.io.FileUtils
import scala.collection.JavaConverters._

/**
 * @author f.patin
 */
object PDF {

  def getCraData(cra: JCra): Array[Byte] = PDFCra.apply(cra)

  def getAbsenceData(absence: JAbsence): Array[Byte] = DbFile.fetch(absence.fileId)

  def createAbsenceData(jAbsences: java.util.List[JAbsence]): Array[Byte] = createAbsenceData(jAbsences.asScala.toList)

  def createCancelAbsenceFile(absence: JAbsence, user: JUser): File = toFile[List[JAbsence]](user, List(absence), PDFAbsence.apply)

  def getOrCreateAbsenceFile(absence: JAbsence, user: JUser): File = getOrCreateAbsenceFile(List(absence), user)

  def getOrCreateAbsenceFile(absences: List[JAbsence], user: JUser): File = toFile[List[JAbsence]](user, absences, PDF.getOrCreateAbsenceData)

  private def getOrCreateAbsenceData(absences: List[JAbsence]): Array[Byte] = {
    if (absences.head.fileId == null) createAbsenceData(absences)
    else DbFile.fetch(absences.head.fileId)
  }

  private def createAbsenceData(absences: List[JAbsence]): Array[Byte] = {
    val data = PDFAbsence(absences)
    val fileId = DbFile.save(data)
    JAbsence.updateFileId(absences.map(_.id).asJava, fileId)
    data
  }

  private def toFile[T](user: JUser, absences: T, f: (T) => Array[Byte]) = {
    val file = newFile(user)
    FileUtils.writeByteArrayToFile(file, f(absences))
    file
  }

  private def newFile(user: JUser) = new File(s"tmp/absence_${user.lastName}_${user.firstName}_${UUID.randomUUID().toString}.pdf".replace(' ', '_'))
}
