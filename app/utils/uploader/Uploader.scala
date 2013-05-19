package utils.uploader

import csv.CSVData

/**
 * @author f.patin
 */
trait Uploader[T] {

  def batchUpload(filename: String) = {
    val csv = new CSVData(filename, separator = ',', skipFirst = true)
    val total = csv.size
    val updated = csv
      .filterNot(exist)
      .map(importOneLine).size
    (total, updated)
  }

  def exist: (Array[String]) => Boolean

  def importOneLine: (Array[String]) => T

}
