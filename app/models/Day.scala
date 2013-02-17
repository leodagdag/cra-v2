package models

import org.joda.time.DateTime
import play.api.Play.current
import play.api.libs.json.JsObject
import play.modules.reactivemongo.PlayBsonImplicits._
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.QueryBuilder
import reactivemongo.api.indexes.Index
import reactivemongo.bson.handlers.DefaultBSONHandlers.{DefaultBSONReaderHandler, DefaultBSONDocumentWriter}
import reactivemongo.bson.{BSONInteger, BSONDateTime, BSONDocument, BSONObjectID}
import reactivemongo.core.commands.LastError
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import reactivemongo.api.indexes.IndexType.Ascending


/**
 * @author f.patin
 */
/**
 * @author f.patin
 */
case class Day(id: Option[BSONObjectID],
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


object Day extends ToIndex {

	private val dbName = "Day"
	private val db = ReactiveMongoPlugin.db.collection(dbName)

	def ensureIndexes {
		import models.ensureIndex
		List(
			Index(List("craId" -> Ascending)),
			Index(List("year" -> Ascending, "month" -> Ascending)),
			Index(List("craId" -> Ascending, "_date" -> Ascending))
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

	def delete(userId: String, date: Long): Future[LastError] = {
		val s = BSONDocument(
			"craId" -> BSONObjectID(userId),
			"_date" -> BSONDateTime(date)
		)
		db.remove(s)
	}

	def delete(userId: String, date: Long, momentOfDay: String): Future[LastError] = {
		val s = BSONDocument(
			"craId" -> BSONObjectID(userId),
			"_date" -> BSONDateTime(date)
		)
		val u = BSONDocument("$unset" -> BSONDocument(momentOfDay -> BSONInteger(1)))
		db.update(s, u)
	}
}
