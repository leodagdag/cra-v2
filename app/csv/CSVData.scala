package csv

/**
 * @author f.patin
 */
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

class CSVData(fileName: String,
              charset: String = "UTF-8", separator: Char = ',', quote: Char = '"', escape: Char = '\\', skipFirst: Boolean = false
               ) extends Traversable[Array[String]] {

  override def foreach[U](f: Array[String] => U) {
    val reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset))
    try {
      if (skipFirst) reader.readLine()
      var next = true
      while (next) {
        val line = reader.readLine()
        if (line != null) f(parse(line))
        else next = false
      }
    } finally {
      reader.close()
    }
  }

  def toMap[T, U](toPair: Array[String] => (T, U)): Map[T, U] = {
    val mapBuilder = Map.newBuilder[T, U]
    for (row <- this) mapBuilder += toPair(row)
    mapBuilder.result()
  }

  private def parse(line: String): Array[String] = {
    val values = Array.newBuilder[String]
    val buffer = new StringBuilder
    var insideQuotes = false
    var escapeNext = false
    for (c <- line) {
      if (escapeNext) { buffer += c; escapeNext = false }
      else if (c == escape) escapeNext = true
      else if (c == quote) insideQuotes = !insideQuotes
      else if (c == separator && !insideQuotes) { values += buffer.result; buffer.clear() }
      else buffer += c
    }
    values += buffer.result
    values.result()
  }

}
