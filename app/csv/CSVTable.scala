package csv

/**
 * @author f.patin
 */
class CSVTable[A](csvFile: CSVData, toObject: Array[String] => A) extends Traversable[A] {

  override def foreach[U](f: A => U) {
    csvFile.foreach(line => f(toObject(line)))
  }

  def toMap[T, U](toPair: A => (T, U)): Map[T, U] = {
    val mapBuilder = Map.newBuilder[T, U]
    for (row <- this) mapBuilder += toPair(row)
    mapBuilder.result()
  }

}
