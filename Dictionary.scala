
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

class Dictionary(totalPopulationState: Int) {
  val statename: String 
  val totalPopulation = totalPopulationState

  // do we even need a master list of labels...?
  // prolly not if we have ageList + raceList + etc
  val labels: ListBuffer [String]
  val dependencies: ListBuffer[String]

  val ageList =  ListBuffer[String]
  val raceList = ListBuffer[String]
  // can have other lists here too .... 




// Map[population, (isFemale, [metadata list])]
  //val dependencies : Map[Int, (Boolean, ListBuffer[String])] 
  // TODO: Add here: addMetadataLabelshit
  // need to store all of the metadata stuff here

  def addLabel(labelName: String) 
  {
    labels += labelName
  }

  def addAgeLabel(ageLabel: String) {
    ageList += ageLabel
    labels += ageLabel 
  }
  //etc

  def addDependencies(dependency: String) {
    dependencies += dependency
  }

  }



// TODO: Make a class for Dependency and for Label
object Dictionary {
  val totalPopMass = 6547629
  //labels are just strings
  // dependencies are also just strings...for now...
  def fromData(dependencies: Map[Int, (Boolean, ListBuffer[String])], labels: Traversable[String]) = {
    val result = new Dictionary(totalPopMass)
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
