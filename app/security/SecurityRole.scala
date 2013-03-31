package security

import be.objectify.deadbolt.core.models.Role

/**
 * @author f.patin
 */
case class SecurityRole(code: String) extends Role {
	override def getName: String = code
}

object BatchRole extends Role {
  def getName: String = SecurityRole.batch
}

object SecurityRole {
	val administrator = "admin"
	val production = "production"
	val employee = "employee"
  val batch = "batch"
}
