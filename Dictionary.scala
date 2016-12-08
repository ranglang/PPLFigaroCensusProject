
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

class Dictionary(totalPopulation: Int) {
  val labels: ListBuffer [String]
  val statename: String 
  val totalPopulation = totalPopulation


  // TODO: Add here: addMetadataLabelshit
  // need to store all of the metadata stuff here

  def addLabel(labelName: String)
  {
    labels += labelName
  }

}

object Dictionary {
  // add all of the lables to the dictionary 
  def fromLabels(labels: Traversable[Labels]) = {
    val result = new Dictionary(0)
    for { label <- labels } { result.addLabel(label) }
    result
  }

  val stopWordFraction = 0.15
  val numFeatures = 100

  def main(args: Array[String]) = {
    /*val emails = LearningComponent.readEmails("Training Emails - 50")
    val dict = Dictionary.fromEmails(emails.map(_._2))
    println("Total number of words: " + dict.words.length)
    println("Number of feature words: " + dict.featureWords.length)
    println("\nAll words and counts:\n")
    println(dict.words.map(word => word + " " + dict.getCount(word)).mkString("\n"))
    println("\nFeature words and counts:\n")
    println("Feature words:\n")
    println(dict.featureWords.map(word => word + " " + dict.getCount(word)).mkString("\n"))*/
  }
}
