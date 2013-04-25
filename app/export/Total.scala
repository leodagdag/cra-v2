package export

import models.{JDay, JMission, JCra}

/**
 * @author f.patin
 */
trait Total {

  val cra: JCra


}

case class EmployeeTotal(cra: JCra) extends Total {

}

case class ProductionTotal(cra: JCra, mission: JMission) extends Total {

}

case class MissionTotal(cra: JCra, mission: JMission) extends Total {

}
