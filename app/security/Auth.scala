package security

import be.objectify.deadbolt.core.models.{Subject, Permission}
import models.User
import play.libs.Scala
import reactivemongo.api.QueryBuilder
import reactivemongo.bson.handlers.DefaultBSONHandlers.{DefaultBSONDocumentWriter, DefaultBSONReaderHandler}
import reactivemongo.bson.handlers.{BSONWriter, BSONReader}
import reactivemongo.bson.{BSONInteger, BSONString, BSONDocument, BSONObjectID}
import scala.concurrent.ExecutionContext.Implicits.global
import concurrent.Future


/**
 * @author f.patin
 */
case class Auth(id: Option[BSONObjectID],
                username: String,
                role: String) extends Subject {
	def getRoles: java.util.List[SecurityRole] = Scala.asJava(List(SecurityRole(role)))

	def getPermissions: java.util.List[_ <: Permission] = ???

	def getIdentifier: String = username
}

object Auth {

	private val db = User.db

	implicit object AuthBSONReader extends BSONReader[Auth] {
		def fromBSON(document: BSONDocument): Auth = {
			val doc = document.toTraversable
			Auth(
				doc.getAs[BSONObjectID]("_id"),
				doc.getAs[BSONString]("username").get.value,
				doc.getAs[BSONString]("role").get.value
			)
		}
	}


	implicit object AuthBSONWriter extends BSONWriter[Auth] {
		def toBSON(auth: Auth) = {
			BSONDocument(
				"_id" -> auth.id.getOrElse(BSONObjectID.generate),
				"username" -> BSONString(auth.username),
				"role" -> BSONString(auth.role)
			)
		}
	}

	def checkAuthentication(auth: (String, String)): Future[Option[Auth]] = {
		val s = BSONDocument(
			"username" -> BSONString(auth._1),
			"password" -> BSONString(auth._2)
		)
		val p = BSONDocument(
			"_id" -> BSONInteger(1),
			"username" -> BSONInteger(1),
			"role" -> BSONInteger(1)
		)
		val q = QueryBuilder()
			.query(s)
			.projection(p)
		db.find[Auth](q).headOption
	}

	def asSubject(username: String) = {
		val s = BSONDocument("username" -> new BSONString(username))
		val q = QueryBuilder()
			.query(s)
		db.find[Subject](q).headOption
	}
}

case class Profile(id:String,
                   username: String,
                   role: String) {
}

object Profile {

	object ProfileBSONReader extends BSONReader[Profile] {
		def fromBSON(document: BSONDocument): Profile = {
			val doc = document.toTraversable
			Profile(
				doc.getAs[BSONObjectID]("_id").get.stringify,
				doc.getAs[BSONString]("username").get.value,
				doc.getAs[BSONString]("role").get.value
			)
		}
	}

	def apply(username: String) = {
		val s = BSONDocument("username" -> BSONString(username))
		val p = BSONDocument(
			"_id" -> BSONInteger(1),
			"username" -> BSONInteger(1),
			"role" -> BSONInteger(1)
		)
		val query = QueryBuilder()
			.query(s)
			.projection(p)
		implicit val reader = ProfileBSONReader
		User.db.find[Profile](query).headOption()
	}
}
