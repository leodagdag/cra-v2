package models

import java.util.Locale
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import reactivemongo.api.indexes.Index
import scala.util.Try
import play.api.Logger
import reactivemongo.api.DefaultCollection
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.handlers.BSONReader
import reactivemongo.bson.{BSONObjectID, BSONDocument}

/**
 * @author f.patin
 */
package object models {
	/**
	 * List des object Models à indexer.
	 */
	val modelsToIndex = List[ToIndex](User, Day, Cra)

	def ensureIndex(index: Index)(db: DefaultCollection) {
		db.indexesManager.ensure(index).onComplete {
			case result: Try[Boolean] =>
				if (result.isFailure) {
					Logger.error(s"Checked index ${index.key} for [${db.name}], result is $result")
				} else {
					Logger.info(s"Checked index ${index.key} for [${db.name}]")
				}
		}
	}

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
