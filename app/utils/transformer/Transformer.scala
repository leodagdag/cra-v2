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

	// TODO Java conversion
	def dateTime2Date(dts: java.util.List[DateTime]): java.util.List[Date] = {
		dts.asScala.map(_.toDate).asJava
	}

	def extractHolidays(day: java.util.List[JDay]): java.util.List[JHalfDay] = {
		val holidays: ImmutableList[ObjectId] = JMission.getHolidaysMissionId
		day.asScala
			.map(d => List(d.morning, d.afternoon))
			.flatten
			.filter(h => if (h == null) false else holidays.asScala.contains(h.missionId))
			.asJava
	}

	def extractObjectId(models: java.util.List[MongoModel]): java.util.List[ObjectId] = {
		models.asScala
			.map(_.id())
			.asJava
	}
}
