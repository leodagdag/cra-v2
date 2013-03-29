package export

import models.JAbsence

/**
 * @author f.patin
 */
object PDF {

	def absence(absence: JAbsence): Array[Byte] = {
		PDFAbsence(absence)
	}


}
