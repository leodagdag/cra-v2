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
    doc.add(Calendar(cra).compose())
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
    doc.add(Calendar(cra, Some(mission)).compose())
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

case class Total(cra: JCra, mission: Option[JMission] = None) extends PDFTableTools {

  val days = mission match {
    case Some(m) => JDay.find(cra.id, cra.userId, cra.year, cra.month, m.id).toList
    case None => JDay.find(cra.id, cra.userId, cra.year, cra.month, false).toList
  }

  def compose: PdfPTable = {
    val cells: List[PdfPCell] = mission match {
      case Some(m) => {
        noBorderCell(s"Total : ${totalForMission.toString()}", boldUnderlineFont) :: Nil
      }
      case None => {
        totalForEmployee.flatMap {
          t =>
            val missionType: MissionType = MissionType.valueOf(t._1)
            val color = MissionTypeColor.by(missionType)
            val title: PdfPCell = noBorderCell(missionType.label, boldUnderlineFont)
            val value: PdfPCell = noBorderCell(t._2.toString(), frontColor = color.frontColor, backgroundColor = color.backgroundColor)
            title :: value :: Nil
        }.toList :+ noBorderCell("Nb jours ouvrés", boldUnderlineFont) :+ noBorderCell(TimeUtils.nbWorkingDaysInMonth(cra.year, cra.month).toString)
      }
    }
    val table = new PdfPTable(cells.size)
    table.setHorizontalAlignment(LEFT)
    table.setWidthPercentage(100f)
    cells.foreach(table.addCell(_))
    table
  }

  private lazy val totalForMission: BigDecimal = {

    def totalHalfDay(halfDay: JHalfDay) = {
      if (halfDay == null) Zero
      else {
        if (!halfDay.missionIds().contains(mission.get.id)) Zero
        else {
          if (halfDay.isSpecial) ???
          else ZeroPointFive
        }
      }
    }

    def totalDay(day: JDay) = totalHalfDay(day.morning) + totalHalfDay(day.afternoon)

    days.foldLeft(Zero) {
      (acc, day) =>
        if (day.missionIds().contains(mission.get.id)) acc + totalDay(day)
        else acc
    }
  }

  private lazy val totalForEmployee: SortedMap[String, BigDecimal] = {

    val halDays: List[JHalfDay] = days
      .filter(d => d != null && !d.missionIds.isEmpty)
      .flatMap(day => List(day.morning, day.afternoon))
      .filter(hd => hd != null)

    val missionIds = halDays.flatMap(hds => hds.missionIds()).toList

    val missions = JMission.codeAndMissionType(missionIds)

    implicit val ordering: Ordering[MissionType] = Ordering.fromLessThan(_.ordinal > _.ordinal)
    TreeMap(halDays.groupBy(hd => missions.get(hd.missionId).missionType).toList: _*)
      .mapValues(hds => ZeroPointFive * hds.size)

  }
}

case class Calendar(cra: JCra, mission: Option[JMission] = None) extends PDFTableTools {

  val days = mission match {
    case Some(m) => JDay.find(cra.id, cra.userId, cra.year, cra.month, m.id).toList
    case None => JDay.find(cra.id, cra.userId, cra.year, cra.month, false).toList
  }

  def compose(): PdfPTable = {
    val table = new PdfPTable(7)
    table.setWidthPercentage(100f)
    table.getDefaultCell.setBorder(NO_BORDER)

    val first = TimeUtils.firstDateOfMonth(cra.year, cra.month)
    val last = TimeUtils.lastDateOfMonth(first)
    val weeks = TreeMap(days.groupBy(day => day.date.getWeekOfWeekyear).toList: _*)

    implicit val toOrderingDay: Ordering[JDay] = Ordering.fromLessThan(_.date isBefore _.date)
    for (dayCell <- weeks.flatMap(week => toWeek(first, last, TreeSet(week._2.sortBy(day => day): _*)))) {
      table.addCell(dayCell)
    }
    table.completeRow()
    table
  }

  private def toWeek(craFirstDate: DateTime, craLastDate: DateTime, days: TreeSet[JDay]): Seq[PdfPTable] = {
    val daysBeforeCra = 0.until((days.head.date.getDayOfWeek - DateTimeConstants.MONDAY)).map(i => emptyDay)
    val daysOfCra = days.toList.map(day => toDay(day))
    daysBeforeCra ++ daysOfCra
  }

  private def toDay(day: JDay) = {
    val table = new PdfPTable(1)
    table.setWidthPercentage(100f)
    table.addCell(dayHeader(day))
    table.addCell(morning(day))
    table.addCell(afternoon(day))
    table
  }

  private lazy val emptyDay = {
    val table = new PdfPTable(1)
    table.setWidthPercentage(100f)
    table
  }

  private def dayHeader(day: JDay) = {
    val cell = new PdfPCell()
    cell.setHorizontalAlignment(CENTER)
    cell.setBorder(NO_BORDER)
    val text = s"${`EEE dd`.print(day.date)} ${if (TimeUtils.isDayOff(day.date)) ("(Férié)") else ("")}".trim
    cell.addElement(new Phrase(text, boldFont))
    cell
  }

  private def morning(day: JDay) = {
    val cell = halfDay(day.morning)
    cell.setBorder(LEFT_TOP_RIGHT)
    cell
  }

  private def afternoon(day: JDay) = {
    val cell = halfDay(day.afternoon)
    cell.setBorder(RIGHT_BOTTOM_LEFT)
    cell
  }

  private def halfDay(halfDay: JHalfDay): PdfPCell = {
    val cell = halfDayContent(halfDay)
    cell.setHorizontalAlignment(CENTER)
    cell.setBorderColor(BaseColor.GRAY)
    cell
  }

  private def halfDayContent(halfDay: JHalfDay): PdfPCell = {
    if (halfDay != null) {
      if (halfDay.isSpecial) noBorderCell("SPECIAL", normal)
      else {
        mission match {
          case Some(m) => noBorderCell("0,5", normal)
          case None => {
            val mission = JMission.fetch(halfDay.missionId)
            val missionType = MissionType.valueOf(mission.missionType)
            val colors = MissionTypeColor.by(missionType)
            val label = if (MissionType.customer.equals(missionType)) mission.label
            else mission.code
            val hour = if (MissionType.customer.equals(missionType)) halfDay.inGenesisHour().toPlainString
            else dummyContent
            val table = new PdfPTable(2)
            val arr = Array(80f, 20f)
            table.setWidths(arr)
            table.setWidthPercentage(100f)
            if (MissionType.customer.equals(missionType)) {
              table.addCell(noBorderCell(label, frontColor = colors.frontColor, backgroundColor = colors.backgroundColor, hAlign = Element.ALIGN_CENTER))
              table.addCell(noBorderCell(hour))
            } else {
              val cell = noBorderCell(label, frontColor = colors.frontColor, backgroundColor = colors.backgroundColor, hAlign = Element.ALIGN_CENTER)
              cell.setColspan(2)
              table.addCell(cell)
            }
            noBorderCell(table)
          }
        }
      }
    } else noBorderCell(dummyContent)
  }
}


case class ProductionCalendar(cra: JCra, mission: Option[JMission]) {

  val days: List[JDay] = mission match {
    case Some(m) => JDay.find(cra.id, cra.userId, cra.year, cra.month, m.id).toList
    case None => JDay.find(cra.id, cra.userId, cra.year, cra.month, false).toList
  }




  def compose() = {
    implicit val toOrderingDay: Ordering[JDay] = Ordering.fromLessThan(_.date isBefore _.date)

    val table = new PdfPTable(8)
    val weeks: TreeMap[(Integer, Integer), List[JDay]] = TreeMap(days.groupBy(d => (d.year, d.week)).toList: _*)

    for(week <- weeks; day: JDay <- week._2.sortBy(day => day))
    yield table.addCell(toCell(day))


    def toCell(day: JDay) = {
      val table = new PdfPTable(1)
      val cell = new PdfPCell()
      cell
    }

    table.completeRow()
    table
  }

}
private object Signature extends PDFTableTools {
  lazy val signatures = {
    val table = new PdfPTable(3)
    table.setWidthPercentage(100f)
    table.getDefaultCell.setHorizontalAlignment(CENTER)
    table.getDefaultCell.setBorder(NO_BORDER)
    table.addCell(customerSignature)
    table.addCell(employeeSignature)
    table.addCell(genesisSignature)
    (0 to 2).foreach(i => table.addCell(_3colsCell))
    table
  }

  private val _3colsCell = colspanCell(3)


  private lazy val customerSignature = newCell(new Phrase("Signature Client", boldUnderlineFont), CENTER)
  private lazy val employeeSignature = newCell(new Phrase("Signature Collaborateur", boldUnderlineFont), CENTER)
  private lazy val genesisSignature = newCell(new Phrase("Signature GENESIS", boldUnderlineFont), CENTER)

  private def newCell(phrase: Phrase, alignment: Alignment = LEFT, border: Border = NO_BORDER) = {
    val cell = new PdfPCell(phrase)
    cell.setHorizontalAlignment(alignment)
    cell.setBorder(border)
    cell
  }


}

case class Claims(cra: JCra, mission: Option[JMission] = None) extends PDFTableTools {
  private lazy val claims = JClaim.synthesis(cra.userId, cra.year, cra.month)
  private lazy val _synthesis: JMap[String, JMap[ClaimType, String]] = JClaimUtils.synthesis(cra.year, cra.month, claims)
  private lazy val weeks = _synthesis.keySet()
  private lazy val nbWeeks = weeks.size()

  lazy val title = new Phrase("Note de Frais", headerFontBold)

  def synthesis() = {
    val table = new PdfPTable(nbWeeks + 1)
    table.setWidthPercentage(100f)
    table.setHeaderRows(1)
    // Header
    table.addCell(headerCell("Semaine"))
    weeks.foreach(w => table.addCell(headerCell(w)))
    // Body
    val body: JEnumMap[ClaimType, JList[String]] = new java.util.EnumMap[ClaimType, JList[String]](classOf[ClaimType])
    _synthesis.keySet() foreach {
      week => {
        for (claimKey: (ClaimType, String) <- _synthesis.get(week)) {
          if (!body.containsKey(claimKey._1)) {
            body.put(claimKey._1, new JArrayList[String]())
          }
          body.get(claimKey._1).add(claimKey._2)
        }
      }
    }

    body.foreach {
      line =>
        table.addCell(headerCell(line._1.label.capitalize))
        line._2.foreach {
          amount =>
            if ("0".equals(amount)) table.addCell(bodyCell(dummyContent, CENTER))
            else table.addCell(bodyCell(amount, CENTER))
        }
    }
    table
  }

  def details() = {
    val table = new PdfPTable(5)
    table.setWidthPercentage(100f)
    table.setHeaderRows(1)
    table.addCell(headerCell("Date"))
    table.addCell(headerCell("Mission"))
    table.addCell(headerCell("Type"))
    table.addCell(headerCell("Montant"))
    table.addCell(headerCell("Commentaire"))


    claims.sortBy(c => c.date)
      .foreach {
      c =>
        table.addCell(bodyCell(`dd/MM/yyyy`.print(c.date), LEFT))
        table.addCell(bodyCell(JMission.codeAndMissionType(c.missionId).label, LEFT))
        table.addCell(bodyCell(ClaimType.valueOf(c.claimType).label.capitalize, LEFT))
        table.addCell(bodyCell(c.amount.toPlainString, RIGHT))
        table.addCell(bodyCell(c.comment, LEFT))
    }
    table

  }
}

case class Comment(cra: JCra) extends PDFTableTools with PDFFont {

  lazy val days = JDay.find(cra.id, cra.userId, cra.year, cra.month, false).toList

  def compose() = {

    val table = new PdfPTable(2)
    table.setHorizontalAlignment(Element.ALIGN_LEFT)
    if(cra.comment != null){
      table.addCell(noBorderCell("Commentaire (mensuel)",boldUnderlineFont, vAlign = Element.ALIGN_TOP))
      table.addCell(noBorderCell(cra.comment, vAlign = Element.ALIGN_TOP))
    }

    days.foreach {
      d =>
        if (d.comment != null) {
          table.addCell(noBorderCell(s"Commentaire du ${`dd/MM/yyyy`.print(d.date)}", boldUnderlineFont))
          table.addCell(noBorderCell(d.comment, vAlign = Element.ALIGN_TOP))
        }
    }
    table
  }
}