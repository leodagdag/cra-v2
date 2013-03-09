package utils.transformer

import com.google.common.collect.ImmutableList
import models.{MongoModel, JMission, JDay}
import org.bson.types.ObjectId
import scala.collection.JavaConverters._
import org.joda.time.DateTime

/**
 * @author f.patin
 */
object Transformer {

	def extractDates(days: java.util.List[JDay]): java.util.List[java.util.Date] = {
		days.asScala
			.map(_.date.toDate)
			.asJava
	}

	def extractHolidays(days: java.util.List[JDay]): java.util.List[JDay] = {
		val holidays: ImmutableList[ObjectId] = JMission.getAbsencesMissionIds
		days.asScala
			.foreach{
			d =>
				val m = d.morning
				if (m != null && !holidays.asScala.contains(m.missionId)) {
					d.morning = null
				}
				val a = d.afternoon
				if (a != null && !holidays.asScala.contains(a.missionId)) {
					d.afternoon = null
				}
		}
			days
	}

	def extractObjectId(models: java.util.List[MongoModel]): java.util.List[ObjectId] = {
		models.asScala
			.map(_.id)
			.asJava
	}

	def setCraId(days: java.util.List[JDay], craId: ObjectId): java.util.List[JDay] = {
		days.asScala
			.foreach(_.craId = craId)
		days
	}


}
