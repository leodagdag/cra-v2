package models

import org.bson.types.ObjectId
import leodagdag.play2morphia.MorphiaPlugin
import java.io.ByteArrayOutputStream


/**
 * @author f.patin
 */
object DbFile {

  def fetch(id: ObjectId): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    MorphiaPlugin.gridFs().findOne(id).writeTo(out)
    out.toByteArray
  }

  def save(b: Array[Byte]): ObjectId = {
    val file = MorphiaPlugin.gridFs().createFile(b)
    file.save()
    ObjectId.massageToObjectId(file.getId)
  }
}
