package utils.transformer

import models.JClaim
import com.google.common.collect.ImmutableList
import scala.collection.JavaConverters._
import org.joda.time.DateTime.Property
import collection.mutable
import java.util

/**
 * @author f.patin
 */
object ClaimUtils {

  def transform(c: ImmutableList[JClaim])  = {
	  val a = List(c.asScala).flatten
	  val weeks  = a.groupBy(_.date.getWeekOfWeekyear)
	  val result  = for(
		  week <- weeks;
		  claims <- List(week._2).flatten.groupBy(_.claimType).toMap
	  ) yield (week._1, claims)
	  result
  }

}
