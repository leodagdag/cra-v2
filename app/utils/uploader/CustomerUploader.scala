package utils.uploader

import java.util.Locale
import models.JCustomer

/**
 *
 * format : "CODE", "NAME"
 * @author f.patin
 */
object CustomerUploader extends Uploader[JCustomer] {

  def exist: (Array[String]) => Boolean = {
    line: Array[String] =>
      JCustomer.exist(line(0))
  }

  /**
   * @return an imported customer
   */
  def importOneLine: (Array[String]) => JCustomer = {
    line: Array[String] =>
      val customer = new JCustomer()
      customer.code = line(0).toUpperCase(Locale.FRANCE)
      customer.name = line(1)
      customer.insert()
      customer
  }

}
