package security

import be.objectify.deadbolt.core.models.{Subject, Permission}
import reactivemongo.bson.{BSONDocumentWriter, BSONDocumentReader, BSONDocument, BSONObjectID}
import scala.concurrent.ExecutionContext.Implicits.global
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current
import http.Etag
import java.util.{List => JList}
import scala.collection.convert.WrapAsJava._
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.Future

/**
 * @author f.patin
 */
case class Auth(id: Option[BSONObjectID],
                username: String,
                role: String) extends Subject {
  def getRoles: JList[SecurityRole] = List(SecurityRole(role))

  def getPermissions: JList[_ <: Permission] = ???

  def getIdentifier: String = username

}

object Auth {

  implicit object AuthBSONReader extends BSONDocumentReader[Auth] {
    def read(doc: BSONDocument): Auth =
      Auth(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("username").get,
        doc.getAs[String]("role").get)
  }

  implicit object AuthBSONWriter extends BSONDocumentWriter[Auth] {
    def write(auth: Auth): BSONDocument =
      BSONDocument(
        "_id" -> auth.id.getOrElse(BSONObjectID.generate),
        "username" -> auth.username,
        "role" -> auth.role)
  }

  def collection = ReactiveMongoPlugin.db.collection[BSONCollection]("User")

  def checkAuthentication(auth: (String, String)): Future[Option[Auth]] = {

    val query = BSONDocument(
      "username" -> auth._1,
      "password" -> auth._2
    )
    val projection = BSONDocument(
      "_id" -> 1,
      "username" -> 1,
      "role" -> 1
    )
    collection.find(query, projection).one[Auth]
  }

  def asSubject(username: String): Future[Option[Auth]] = {

    val query = BSONDocument(
      "username" -> username
    )
    collection.find(query).one[Auth]
  }
}

case class Profile(id: Option[String],
                   username: String,
                   role: String) extends Etag {
}

object Profile {

  def collection = ReactiveMongoPlugin.db.collection[BSONCollection]("User")

  implicit object ProfileBSONReader extends BSONDocumentReader[Profile] {
    def read(doc: BSONDocument): Profile =
      Profile(
        doc.getAs[BSONObjectID]("_id").map(_.stringify),
        doc.getAs[String]("username").get,
        doc.getAs[String]("role").get
      )
  }

  def apply(username: String): Future[Option[Profile]] = {
    val query = BSONDocument("username" -> username)
    collection.find(query).one[Profile]
  }
}
