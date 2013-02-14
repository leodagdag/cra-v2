package utils.transformer

import org.joda.time.DateTime
import scala.collection.JavaConverters._

/**
 * @author f.patin
 */
object Transformer {

	// TODO Java conversion
	def dateTime2Date(dts: java.util.List[DateTime]) = {
		dts.asScala.map(_.toDate).asJava
	}
}
