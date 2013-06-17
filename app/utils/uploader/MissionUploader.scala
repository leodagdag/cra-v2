package utils.uploader

import models.{JAffectedMission, JUser, JCustomer, JMission}
import scala.collection.convert.WrapAsScala._
import java.util.Locale
import constants.{MissionAllowanceType, MissionType}
import org.joda.time.DateTime
import utils._
import org.apache.commons.lang3.StringUtils

/**
 * format : "USERNAME(0)","CODE CLIENT(1)","FORFAIT(2)","CODE(3)","DESCRIPTION(4)","Debut(5)","Fin(6)"
 * @author f.patin
 */
object MissionUploader extends Uploader[JMission] {

  def exist: (Array[String]) => Boolean = {
    line =>
      val customer = JCustomer.byCode(line(1))
      if (customer == null) {
        true
      } else {
        val mission = JMission.fetch(customer.id, line(3))
        if (mission == null) {
          false
        } else {
          val user = JUser.byUsername(line(0))
          if (user == null) {
            false
          } else {
            user.affectedMissions.find(am => am.missionId.equals(mission.id)) match {
              case None => false
              case _ => true
            }
          }
        }
      }
  }

  def importOneLine: (Array[String]) => JMission = {
    line =>
      val customer = JCustomer.byCode(line(1))

      val user = JUser.byUsername(line(0))
      val mission = if (JMission.exist(customer.id, line(3))) {
        JMission.fetch(customer.id, line(3))
      } else {
        val m = new JMission()
        m.code = line(3).toUpperCase(Locale.FRANCE)
        m.label = line(3)
        m.customerId = customer.id
        m.description = line(4)
        m.missionType = MissionType.customer.name()
        m.isClaimable = true
        m.insert()
      }


      val affectedMission = new JAffectedMission(mission)
      affectedMission.startDate =  DateTime.parse(line(5), `dd/MM/yyyy`)
      if (StringUtils.isNotBlank(line(6))) affectedMission.endDate = DateTime.parse(line(6), `dd/MM/yyyy`)
      val l2 = line(2).toUpperCase
      affectedMission.allowanceType =
        if (StringUtils.isNotBlank(l2))
          if (l2.equals(MissionAllowanceType.ZONE.name())) MissionAllowanceType.ZONE.name()
          else if (StringUtils.isNumeric(l2)){
            affectedMission.feeAmount = BigDecimal(l2).bigDecimal
            MissionAllowanceType.FIXED.name()
          }
          else MissionAllowanceType.NONE.name()
        else MissionAllowanceType.NONE.name()

      user.affectedMissions.add(affectedMission)
      user.update()
      mission
  }
}
