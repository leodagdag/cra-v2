package export

import java.io.File
import java.util.UUID
import models.{JUser, DbFile, JAbsence}
import org.apache.commons.io.FileUtils
import scala.collection.JavaConverters._
import mail.Mailer

/**
 * @author f.patin
 */
object PDF {


  def absence(absence: JAbsence): Array[Byte] = {
    if (absence.fileId == null) {
      val data = PDFAbsence.compose(absence)
      val fileId = DbFile.save(data)
      JAbsence.updateFileId(absence.id, fileId)
      data
    } else {
      DbFile.fetch(absence.fileId)
    }
  }

  def absence(jAbsences: java.util.List[JAbsence]): Array[Byte] = {
    absence(jAbsences.asScala.toList)
  }

  def createFile(jAbsences: java.util.List[JAbsence]): Array[Byte] = {
    createFile(jAbsences.asScala.toList)
  }

  def createFile(absences: List[JAbsence]): Array[Byte] = {
    val data = PDFAbsence.compose(absences)
    val fileId = DbFile.save(data)
    JAbsence.updateFileId(absences.map(_.id).asJava, fileId)
    data
  }

  def absence(absences: List[JAbsence]): Array[Byte] = {
    val first = absences.head
    if (first.fileId == null) {
      createFile(absences)
    } else {
      DbFile.fetch(first.fileId)
    }
  }

  private def newFile(user: JUser) = new File(s"tmp/absence_${user.lastName}_${user.firstName}_${UUID.randomUUID().toString}.pdf".replace(' ', '_'))

  def absenceFile(absence: JAbsence, user: JUser): File = {
    val file = newFile(user)
    FileUtils.writeByteArrayToFile(file, PDF.absence(absence))
    file
  }

  def absenceFile(absences: java.util.List[JAbsence], user: JUser): File = {
    val file = newFile(user)
    FileUtils.writeByteArrayToFile(file, PDF.absence(absences))
    file
  }

  def absenceFile(absences: List[JAbsence], user: JUser): File = {
    val file = newFile(user)
    FileUtils.writeByteArrayToFile(file, PDF.absence(absences))
    file
  }

  def createCancelAbsenceFile(absence: JAbsence, user: JUser): File = {
    val file = newFile(user)
    FileUtils.writeByteArrayToFile(file, PDFAbsence.compose(absence))
    file
  }
}
