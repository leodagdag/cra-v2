package utils.uploader

import constants.{MissionType, MissionAllowanceType}
import java.util.Locale
import models.{JAffectedMission, JUser, JCustomer, JMission}
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import utils._

/**
 * format : "USERNAME(0)","CODE CLIENT(1)","FORFAIT(2)","CODE(3)","DESCRIPTION(4)","Debut(5)","Fin(6)"
 * @author f.patin
 */
object MissionUploader extends Uploader[JMission] {

  def exist: (Array[String]) => Boolean = {
    line =>
      val customer = JCustomer.byCode(line(1))
      JMission.exist(customer.id, line(5))
  }

  def importOneLine: (Array[String]) => JMission = {
    line =>
      val customer = JCustomer.byCode(line(1))
      val user = JUser.byUsername(line(0))
      val mission = new JMission()
      mission.code = line(3).toUpperCase(Locale.FRANCE)
      mission.label = line(3)
      mission.customerId = customer.id
      mission.description = line(4)
      mission.missionType = MissionType.customer.name()
      mission.isClaimable = true
      mission.startDate = DateTime.parse(line(5), `dd/MM/yyyy`)
      if (StringUtils.isNotBlank(line(6))) mission.endDate = DateTime.parse(line(6), `dd/MM/yyyy`)
      mission.insert()

      val affectedMission = new JAffectedMission(mission)
      val l2 = line(2).toUpperCase
      affectedMission.allowanceType =
        if (StringUtils.isNotBlank(l2))
          if (l2.equals(MissionAllowanceType.ZONE.name())) MissionAllowanceType.ZONE.name()
          else if (StringUtils.isNumeric(l2)) MissionAllowanceType.FIXED.name()
          else MissionAllowanceType.NONE.name()
        else MissionAllowanceType.NONE.name()
      if (StringUtils.isNumeric(l2)) affectedMission.feeAmount = BigDecimal(l2).bigDecimal

      user.affectedMissions.add(affectedMission)
      user.update()
      mission
  }
}
