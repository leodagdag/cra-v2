package models

import play.api.Play.current
import play.api.libs.json.JsObject
import play.modules.reactivemongo.PlayBsonImplicits._
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.QueryBuilder
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.bson.handlers.DefaultBSONHandlers.{DefaultBSONDocumentWriter, DefaultBSONReaderHandler}
import reactivemongo.bson.{BSONObjectID, BSONBoolean, BSONDocument, BSONInteger, BSONString}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * @author f.patin
 */

case class User(id: Option[BSONObjectID],
                username: String,
                password: String,
                role: String,
                firstName: String,
                lastName: String,
                trigramme: String,
                email: String,
                managerId: Option[BSONObjectID],
                isManager: Boolean)


object User extends ToIndex {

	private val dbName = "User"

	val db = ReactiveMongoPlugin.db.collection(dbName)

	override def ensureIndexes {
		import models.ensureIndex
		List(
			Index(List("username" -> Ascending), unique = true),
			Index(List("username" -> Ascending, "password" -> Ascending), unique = true),
			Index(List("lastName" -> Ascending, "firstName" -> Ascending)),
			Index(List("isManager" -> Ascending))
		).foreach {
			index => ensureIndex(index)(db)
		}
	}


	def all = {
		val criteria = BSONDocument()
		val query = QueryBuilder()
			.query(criteria)
		implicit val readerO = JsObjectReader
		User.db.find[JsObject](query).toList()
	}

	def id(username: String) = {
		val s = BSONDocument("username" -> BSONString(username))
		val p = BSONDocument("_id" -> BSONInteger(1))
		val q = QueryBuilder()
			.query(s)
			.projection(p)
		implicit val reader =  models.BSONObjectIDReader
		db.find[BSONObjectID](q).headOption()

	}
}


object Manager {
	def managers: Future[List[JsObject]] = {
		val p = BSONDocument(
			"_id" -> BSONInteger(1),
			"firstName" -> BSONInteger(1),
			"lastName" -> BSONInteger(1)
		)
		implicit val reader = JsObjectReader
		val query = QueryBuilder()
			.query(BSONDocument("isManager" -> new BSONBoolean(true)))
			.projection(p)
		User.db.find[JsObject](query).toList()
	}
}

object Account {

	def accountByUsername(username: String): Future[Option[JsObject]] = {
		val p = BSONDocument(
			"_id" -> BSONInteger(1),
			"username" -> BSONInteger(1),
			"firstName" -> BSONInteger(1),
			"lastName" -> BSONInteger(1),
			"trigramme" -> BSONInteger(1),
			"email" -> BSONInteger(1),
			"isManager" -> BSONInteger(1)
		)
		implicit val reader = JsObjectReader
		val query = QueryBuilder()
			.query(BSONDocument("username" -> new BSONString(username)))
			.projection(p)
		User.db.find[JsObject](query).headOption
	}

}
