package models

import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID

/**
 * @author f.patin
 */
case class AffectedMission(startDate: DateTime,
                           endDate: DateTime,
                           missionId: BSONObjectID,
                           allowanceType: String,
                           feeAmount: Double
                            ) {
}
