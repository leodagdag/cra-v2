package models

import java.io.ByteArrayOutputStream
import leodagdag.play2morphia.MorphiaPlugin
import org.bson.types.ObjectId


/**
 * @author f.patin
 */
object DbFile {

  def fetch(id: ObjectId): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    MorphiaPlugin.gridFs().findOne(id).writeTo(out)
    out.toByteArray
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
