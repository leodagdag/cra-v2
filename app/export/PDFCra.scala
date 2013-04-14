package export


import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable}
import com.itextpdf.text.{BaseColor, Phrase, Paragraph, PageSize, Document}
import constants.{ClaimType, MissionTypeColor, MissionType}
import java.util.{Map => JMap, EnumMap => JEnumMap, List => JList, ArrayList => JArrayList}
import models.{JClaim, JHalfDay, JDay, JUser, JMission, JCra}
import org.bson.types.ObjectId
import org.joda.time.{DateTimeConstants, DateTime}
import scala.Some
import scala.collection.convert.WrapAsJava._
import scala.collection.convert.WrapAsScala._
import scala.collection.immutable.{SortedMap, TreeSet, TreeMap, List}
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
    doc.add(Calendar(cra).compose())
    doc.add(PDFCraTools.blankLine)
    doc.add(Total(cra).compose)
    doc.add(PDFCraTools.blankLine)
    // Claims
    doc.add(new LineSeparator())
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
    doc.add(Calendar(cra, Some(mission)).compose)
    doc.add(PDFCraTools.blankLine)
    doc.add(Total(cra, Some(mission)).compose)
    doc.add(PDFCraTools.blankLine)
    doc.add(Signature.signatures)
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
        phraseln(phrase("Période : ", headerFont), phrase(new DateTime(year, month, 1, 0, 0).toString("MMMM YYYY").capitalize, headerFontBold)),
        blankLine,
        mission.map(m => phraseln(phrase("Mission : ", headerFont), phrase(s"${m.label}", headerFontBold))).getOrElse(new Phrase(""))
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
            val value: PdfPCell = noBorderCell(t._2.toString, frontColor = color.frontColor, backgroundColor = color.backgroundColor)
            title :: value :: Nil
        }.toList :+ noBorderCell("Nb jours ouvrés", boldUnderlineFont) :+ noBorderCell(TimeUtils.nbWorkingDaysInMonth(cra.year, cra.month).toString)
      }
    }
    val table = new PdfPTable(cells.size)
    table.setHorizontalAlignment(LEFT)
    table.setWidthPercentage(100f)
    //table.getDefaultCell.setBorder(NO_BORDER)
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
    val cell = toHalfDay(day.morning)
    cell.setBorder(LEFT_TOP_RIGHT)
    cell
  }

  private def afternoon(day: JDay) = {
    val cell = toHalfDay(day.afternoon)
    cell.setBorder(RIGHT_BOTTOM_LEFT)
    cell
  }

  private def toHalfDay(halfDay: JHalfDay): PdfPCell = {
    val cell = halfDayContent(halfDay)
    cell.setHorizontalAlignment(CENTER)
    cell.setBorderColor(BaseColor.GRAY)
    cell
  }

  private def halfDayContent(halfDay: JHalfDay) = {
    if (halfDay != null) {
      if (halfDay.isSpecial) noBorderCell("SPECIAL", normal)
      else {
        mission match {
          case Some(m) => noBorderCell("0,5", normal)
          case None => {
            val mission: JMission = JMission.fetch(halfDay.missionId)
            val colors = MissionTypeColor.by(MissionType.valueOf(mission.missionType))
            noBorderCell(mission.label, normal, colors.frontColor, colors.backgroundColor)
          }
        }
      }
    } else noBorderCell(dummyCellContent)
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
            if ("0".equals(amount)) table.addCell(bodyCell(dummyCellContent, CENTER))
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