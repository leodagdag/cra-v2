package models

import org.joda.time.DateTime
import play.api.Play.current
import play.api.libs.json.JsObject
import play.modules.reactivemongo.PlayBsonImplicits._
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.QueryBuilder
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler
import reactivemongo.bson.{BSONInteger, BSONDateTime, BSONDocument, BSONObjectID}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author f.patin
 */
case class Cra(id: Option[BSONObjectID],
               userId: BSONObjectID,
               year: Int,
               month: Int,
               comment: String,
               validated: Boolean) {

}

/**
 * @author f.patin
 */
case class Day(id: Option[BSONObjectID],
               userId: BSONObjectID,
               craId: BSONObjectID,
               _date: DateTime,
               year: Int,
               month: Int,
               morning: Option[HalfDay],
               afternoon: Option[HalfDay],
               comment: String) {

}

case class HalfDay(missionId: Option[BSONObjectID],
                   periods: Option[List[Period]]) {

	def isSpecial: Boolean = missionId.map(_ => true).getOrElse(false)
}

case class Period(missionId: BSONObjectID,
                  startTime: DateTime,
                  endTime: DateTime) {

}

object Cra extends ToIndex {
	private val dbName = "Cra"
	val db = ReactiveMongoPlugin.db.collection(dbName)

	def ensureIndexes {
		import models.ensureIndex
		List(
			Index(List("userId" -> Ascending)),
			Index(List("year" -> Ascending, "month" -> Ascending)),
			Index(List("validated" -> Ascending))
		).foreach(index => ensureIndex(index)(db))
	}
}

object Day extends ToIndex {
	private val dbName = "Day"
	val db = ReactiveMongoPlugin.db.collection(dbName)

	def ensureIndexes {
		import models.ensureIndex
		List(
			Index(List("userId" -> Ascending)),
			Index(List("craId" -> Ascending)),
			Index(List("year" -> Ascending, "month" -> Ascending)),
			Index(List("date" -> Ascending))
		).foreach {
			index => ensureIndex(index)(db)
		}
	}

	def fetch(userId: String, year: Int, month: Int, day: Int) = {
		val criterias = BSONDocument(
			"craId" -> BSONObjectID(userId),
			"_date" -> BSONDateTime(new DateTime(2013, 3, 1, 0, 0).getMillis)
		)
		implicit val reader = JsObjectReader
		val q = QueryBuilder().query(criterias)
		db.find[JsObject](q).headOption
	}
}
