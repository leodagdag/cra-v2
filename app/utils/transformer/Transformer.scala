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

	def toDates(days: java.util.List[JDay]): java.util.List[java.util.Date] = {
		days.asScala
			.map(_.date.toDate)
			.asJava
	}
	def fromDateTimes(dts: java.util.Set[DateTime]): java.util.Set[java.util.Date] = {
		dts.asScala
			.map(_.toDate)
			.asJava
	}

	def removeHolidays(days: java.util.List[JDay]): java.util.List[JDay] = {
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

	def toObjectId(models: java.util.List[MongoModel]): java.util.List[ObjectId] = {
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
