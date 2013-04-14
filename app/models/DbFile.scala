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

  def save(b: Array[Byte], ct: String = "application/pdf"): ObjectId = {
    val file = MorphiaPlugin.gridFs().createFile(b)
    file.setContentType(ct)
    file.save()
    ObjectId.massageToObjectId(file.getId)
  }

  def remove(id: ObjectId) {
    MorphiaPlugin.gridFs().remove(id)
  }
}
