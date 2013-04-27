package export

import com.itextpdf.text.Font

/**
 * @author f.patin
 */
object FontTools {

  val baseFont: Font = new Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)

  val boldUnderlineFont: Font = {
    val f = new Font(baseFont)
    f.setStyle(Font.UNDERLINE + Font.BOLD)
    f
  }
}
