package models

import java.io.ByteArrayOutputStream
import leodagdag.play2morphia.MorphiaPlugin
import org.bson.types.ObjectId
import play.libs.F


/**
 * @author f.patin
 */
object DbFile {

  def fetch(id: ObjectId): F.Tuple[ObjectId,Array[Byte]] = {
    val out = new ByteArrayOutputStream()
    MorphiaPlugin.gridFs().findOne(id).writeTo(out)
    F.Tuple(id, out.toByteArray)
  }

  def fileName(id: ObjectId): String = {
    MorphiaPlugin.gridFs().findOne(id).getFilename
  }

  def save(b: Array[Byte], fileName: String, ct: String = "application/pdf"): ObjectId = {
    val file = MorphiaPlugin.gridFs().createFile(b)
    file.setContentType(ct)
    file.setFilename(fileName)
    file.save()
    ObjectId.massageToObjectId(file.getId)
  }

  def remove(id: ObjectId) {
    MorphiaPlugin.gridFs().remove(id)
  }
}
