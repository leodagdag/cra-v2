package export

import models.{JUser, DbFile, JAbsence}
import java.io.File
import java.util.UUID
import org.apache.commons.io.FileUtils
import mail.Body

/**
 * @author f.patin
 */
object PDF {

  def absence(absence: JAbsence): Array[Byte] = {
    if (absence.fileId == null) {
      val file = PDFAbsence(absence)
      val fileId = DbFile.save(file)
      JAbsence.updateFileId(absence.id, fileId)
      file
    } else {
      DbFile.fetch(absence.fileId)
    }
  }

  def absenceFile(absence: JAbsence, user: JUser): File = {
    val file = new File(s"tmp/absence_${user.lastName}_${user.firstName}_${UUID.randomUUID().toString}.pdf".replace(' ','_'))
    FileUtils.writeByteArrayToFile(file, PDF.absence(absence))
    file
  }


}
