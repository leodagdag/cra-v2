package models

import java.util.Locale
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import reactivemongo.bson.handlers.BSONReader
import reactivemongo.bson.{BSONObjectID, BSONDocument}

/**
 * @author f.patin
 */
package object models {

  private val dtf = DateTimeFormat.forPattern("dd/MM/yyyy").withLocale(Locale.FRANCE)

  def toDateTime(date: String): DateTime = {
    DateTime.parse(date, dtf)
  }

  object BSONObjectIDReader extends BSONReader[BSONObjectID] {
    def fromBSON(document: BSONDocument): BSONObjectID = {
      val doc = document.toTraversable
      BSONObjectID(
        doc.getAs[BSONObjectID]("_id").get.stringify
      )
    }
  }

}
