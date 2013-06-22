package export


import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.{Phrase, Paragraph, PageSize, Document}
import constants.MissionType
import java.util.{Map => JMap, EnumMap => JEnumMap, List => JList, ArrayList => JArrayList}
import models.{JDay, JUser, JMission, JCra}
import org.bson.types.ObjectId
import org.joda.time.DateTime
import scala.Some
import scala.collection.convert.WrapAsJava._
import scala.collection.convert.WrapAsScala._
import scala.collection.immutable.List
import utils._
import utils.time.TimeUtils

/**
 * @author f.patin
 */

abstract class PDFCra[T]() extends PDFComposer[T] {
  protected def document(): Document = new Document(PageSize.A4.rotate())
}

object PDFEmployeeCra extends PDFCra[JCra] {

  protected def content(doc: Document, cra: JCra) {
    this.cra = cra
    this.user = JUser.account(cra.userId)

    // Header
    doc.add(PDFCraTools.pageHeader(cra.userId))
    // Page
    // Calendar
    doc.add(EmployeeCalendar(cra).compose())
    doc.add(EmployeeTotalCra(cra).compose())
    doc.add(PDFCraTools.blankLine)
    // Comment
    doc.add(EmployeeComment(cra).compose())
    doc.newPage()
    // Claims
    val claims = Claims(cra)
    doc.add(claims.title)
    doc.add(claims.synthesis())
    doc.add(PDFCraTools.blankLine)
    doc.add(claims.details())
  }
}

object PDFMissionCra extends PDFCra[(JCra, JMission)] {

  protected def content(doc: Document, obj: (JCra, JMission)) {
    val cra = obj._1
    val mission = obj._2
    this.cra = cra
    this.user = JUser.account(cra.userId)
    // Header
    doc.add(PDFCraTools.pageHeader(cra.userId, Some(mission)))
    // Page
    doc.add(MissionCalendar(cra, mission).compose())
    doc.add(MissionTotalCra(cra, mission).compose())
    doc.add(Signature.signatures)
  }

}

object PDFProductionCra extends PDFCra[JCra] {

  override protected def document(): Document = new Document(PageSize.A4)

  protected def content(doc: Document, cra: JCra) {
    this.cra = cra
    this.user = JUser.account(cra.userId)


    val days = JDay.fetch(cra).toList
    val a: List[ObjectId] = days
      .flatMap(_.missionIds())
      .toSet
      .toList
    val missions = a.map(id => JMission.fetch(id))
      .filter(m => MissionType.valueOf(m.missionType).equals(MissionType.customer))
      .sortBy(_.code)
      .toList

    missions.foreach {
      m =>
        doc.add(PDFCraTools.pageHeader(cra.userId))
        doc.add(ProductionCalendar(cra, m).compose())
        doc.add(ProductionTotalCra(cra, m).compose())
        doc.add(ProductionTotalClaim(cra, m).compose())
        doc.add(ProductionComment(cra, m).compose())
        doc.newPage()

    }
    val claims = Claims(cra)
    doc.add(claims.title)
    doc.add(claims.synthesis())
    doc.add(claims.details())
    doc.add(claims.totalByCustomerMission())
    doc.newPage()
  }
}

object PDFCraTools extends PDFTableTools with PDFTools with PDFFont {

  private val title = "Rapport d'Activité"

  def pageHeader(userId: ObjectId, mission: Option[JMission] = None): PdfPTable = {
    val § = new Paragraph()
    val user = JUser.identity(userId)
    §.addAll(
      List(
        phraseln(title, titleFont),
        blankLine,
        phraseln(phrase("Collaborateur : ", headerFont), phrase(s"${user.fullName()}", headerFontBold)),
        blankLine,
        phraseln(phrase("Période : ", headerFont), phrase(`MMMM yyyy`.print(TimeUtils.firstDateOfMonth(DateTime.now)).capitalize, headerFontBold)),
        blankLine,
        mission.map(m => phraseln(phrase("Mission : ", headerFont), phrase(s"${m.label}", headerFontBold), blankLine)).getOrElse(new Phrase(dummyContent))
      )
    )
    super.pageHeader(§)
  }

  lazy val signatures = Signature.signatures

}

