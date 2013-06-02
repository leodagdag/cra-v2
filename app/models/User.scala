package models

import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}
import scala.Predef.String
import security.{Auth, SecurityRole}

/**
 * @author f.patin
 */
case class User(id: Option[BSONObjectID],
                username: String,
                trigramme: String,
                firstName: String,
                lastName: String,
                email: String,
                role: String,
                managerId: Option[BSONObjectID],
                isManager: Boolean,
                affectedMissions: Option[List[BSONObjectID]],
                password: String) {
}

object User {
  def collection = ReactiveMongoPlugin.db.collection[BSONCollection]("User")

  implicit object UserBSONReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User =
      User(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("username").get,
        doc.getAs[String]("trigramme").get,
        doc.getAs[String]("firstName").get,
        doc.getAs[String]("lastName").get,
        doc.getAs[String]("email").get,
        doc.getAs[String]("role").get,
        doc.getAs[BSONObjectID]("managerId"),
        doc.getAs[Boolean]("isManager").get,
        doc.getAs[List[BSONObjectID]]("affectedMissions"),
        doc.getAs[String]("password").get)
  }



  def all() = {

  }
}
