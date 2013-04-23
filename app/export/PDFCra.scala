package export


import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text.{Element, BaseColor, Phrase, Paragraph, PageSize, Document}
import constants.{ClaimType, MissionTypeColor, MissionType}
import java.util.{Map => JMap, EnumMap => JEnumMap, List => JList, ArrayList => JArrayList}
import models.{JClaim, JHalfDay, JDay, JUser, JMission, JCra}
import org.bson.types.ObjectId
import org.joda.time.{DateTimeConstants, DateTime}
import scala.Some
import scala.collection.convert.WrapAsJava._
import scala.collection.convert.WrapAsScala._
import scala.collection.immutable.{SortedMap, TreeSet, TreeMap, List}
import utils._
import utils.business.JClaimUtils
import utils.time.TimeUtils

/**
 * @author f.patin
 */

abstract class PDFCra[T]() extends PDFComposer[T] {
  protected def document(): Document = new Document(PageSize.A4.rotate())
}

object PDFEmployeeCra extends PDFCra[JCra] {

  def content(doc: Document, cra: JCra) {
    // Header
    doc.add(PDFCraTools.pageHeader(cra.userId, cra.year, cra.month))
    // Page
    // Calendar
    doc.add(EmployeeCalendar(cra).compose())
    doc.add(PDFCraTools.blankLine)
    doc.add(Total(cra).compose)
    doc.add(PDFCraTools.blankLine)
    // Comment
    if (cra.comment != null) {
      doc.add(Comment(cra).compose())
      doc.add(PDFCraTools.blankLine)
    }
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

  def content(doc: Document, obj: (JCra, JMission)) {
    val cra = obj._1
    val mission = obj._2
    // Header
    doc.add(PDFCraTools.pageHeader(cra.userId, cra.year, cra.month, Some(mission)))
    // Page
    doc.add(MissionCalendar(cra, mission).compose())
    doc.add(PDFCraTools.blankLine)
    doc.add(Total(cra, Some(mission)).compose)
    doc.add(PDFCraTools.blankLine)
    doc.add(Signature.signatures)
  }

}

object PDFProductionCra extends PDFCra[JCra] {

  override protected def document(): Document = new Document(PageSize.A4)

  def content(doc: Document, cra: JCra) {
    doc.add(PDFCraTools.pageHeader(cra.userId, cra.year, cra.month))
    //val days = JDay.find()
    //doc.add(ProductionCalendar(cra).compose)
  }
}

object PDFCraTools extends PDFTableTools with PDFTools with PDFFont {

  private val title = "Rapport d'Activité"

  def pageHeader(userId: ObjectId, year: Int, month: Int, mission: Option[JMission] = None): PdfPTable = {
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
        mission.map(m => phraseln(phrase("Mission : ", headerFont), phrase(s"${m.label}", headerFontBold), blankLine)).getOrElse(new Phrase(dummyContent)),
        phraseln(phrase("Généré le : ", headerFont), phrase(s"${`dd/MM/yyyy à HH:mm:ss`.print(DateTime.now)}", headerFont))
      )
    )
    super.pageHeader(§)
  }

  lazy val signatures = Signature.signatures

}

