package models

import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime


/**
 * @author f.patin
 */
case class Customer(id: Option[BSONObjectID],
                    code: String,
                    name: String,
                    finalCustomerId: Option[BSONObjectID],
                    isGenesis: Boolean = false) {

}

case class Mission(id: Option[BSONObjectID],
                   customerId: BSONObjectID,
                   code: String,
                   description: String,
                   missionType: String,
                   allowanceType: String,
                   startDate: DateTime,
                   endDate: Option[DateTime]) {

}
