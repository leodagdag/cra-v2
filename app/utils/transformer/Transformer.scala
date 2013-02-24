package utils.transformer

import com.google.common.collect.ImmutableList
import java.util.Date
import models.{MongoModel, JMission, JHalfDay, JDay}
import org.bson.types.ObjectId
import org.joda.time.DateTime
import scala.collection.JavaConverters._

/**
 * @author f.patin
 */
object Transformer {



	def extractHolidays(day: java.util.List[JDay]): java.util.List[JHalfDay] = {
		val holidays: ImmutableList[ObjectId] = JMission.getAbsencesMissionIds
		day.asScala
			.flatMap(d => List(d.morning, d.afternoon))
			.filter(h => if (h == null) false else holidays.asScala.contains(h.missionId))
			.asJava
	}

	def extractObjectId(models: java.util.List[MongoModel]): java.util.List[ObjectId] = {
		models.asScala
			.map(_.id)
			.asJava
	}

	def setCraId(days: java.util.List[JDay], craId: ObjectId): java.util.List[JDay] = {
		days.asScala
			.map {
			d =>
				d.craId = craId
				d
		}
			.asJava
	}
}
