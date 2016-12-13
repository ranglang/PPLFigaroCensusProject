
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

class Dictionary(totalPopulationState: Int) {
  val labels: ListBuffer [String]
  val statename: String 
  val totalPopulation = totalPopulationState

// Map[population, (isFemale, [metadata list])]
  val dependencies : Map[Int, (Boolean, ListBuffer[String])] 
  // TODO: Add here: addMetadataLabelshit
  // need to store all of the metadata stuff here

  def addLabel(labelName: String) 
  {
    labels += labelName
  }


  //def addDependency(dependency: ListBuffer[String]) {
    //dependencies += 
    // prolly not best way to add the dependencies?
    //}
  }



// TODO: Make a class for Dependency and for Label
object Dictionary {
  //labels are just strings
  // dependencies are also just strings...for now...
  def fromDependenciesAndLables(dependencies: Map[Int, (Boolean, ListBuffer[String])], labels: Traversable[String]) = {
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
