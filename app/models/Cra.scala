package models

import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.QueryBuilder
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.bson.handlers.DefaultBSONHandlers.{DefaultBSONReaderHandler, DefaultBSONDocumentWriter}
import reactivemongo.bson.{BSONObjectID, BSONBoolean, BSONString, BSONInteger, BSONDocument}
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * @author f.patin
 */
case class Cra(id: Option[BSONObjectID],
               userId: BSONObjectID,
               year: Int,
               month: Int,
               comment: String,
               isValidated: Boolean) {
}

object Cra extends ToIndex {
	private val dbName = "Cra"
	private val db = ReactiveMongoPlugin.db.collection(dbName)

	def ensureIndexes {
		import models.ensureIndex
		List(
			Index(List("userId" -> Ascending)),
			Index(List("year" -> Ascending, "month" -> Ascending)),
			Index(List("validated" -> Ascending))
		).foreach(index => ensureIndex(index)(db))
	}

	def validate(id: String) = {
		val s = BSONDocument("_id" -> BSONObjectID(id))
		val u = BSONDocument("$set" -> BSONDocument("isValidated" -> BSONBoolean(true)))
		db.update(s, u)
	}

	def invalidate(id: String) = {
		val s = BSONDocument("_id" -> BSONObjectID(id))
		val u = BSONDocument("$set" -> BSONDocument("isValidated" -> BSONBoolean(false)))
		db.update(s, u)
	}

	def id(userId: String, year: Int, month: Int) = {
		val s = BSONDocument("userId" -> BSONString(userId),
			"year" -> BSONInteger(year),
			"month" -> BSONInteger(month)
		)
		val p = BSONDocument("_id" -> BSONInteger(1))
		val q = QueryBuilder()
			.query(s)
			.projection(p)
		implicit val reader = models.BSONObjectIDReader
		db.find[BSONObjectID](q).headOption
	}

}

