
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import java.nio.file.{Files,Paths,Path}
import java.io._
import scala.io.Source

/*
 * The Dictionary class holds all of the labels by category
 * At the moment, it only holds a list of labels for age and race, but the
 * hope is that we will eventually have all of the different categories
 * covered.
 *
 * We also have a master list, called labels, with ALL of the labels.
 * 
 * In LearningComponent we will read in the data file and associate
 * the different labels with gender - "isFemale"
*/

class Dictionary(totalPopulationState: Int, stateNameParam: String) {

  // These are member variables of the Dictionary class 
  val statename: String = stateNameParam
  val totalPopulation = totalPopulationState

  // Master list of labels (all of them)
  val labels: ListBuffer [String] = ListBuffer()
  //val dependencies: ListBuffer[String] = ListBuffer()

  val ageList: ListBuffer[String] = ListBuffer()
  val raceList: ListBuffer[String] = ListBuffer()
  // can have other lists here too .... 
  // I just have age and race for now to keep things simple


  def addLabel(labelName: String) 
  {
    labels += labelName
  }

  def addAgeLabel(ageLabel: String) {
    ageList += ageLabel
    addLabel(ageLabel)
  }

  def addRaceLabel(raceLabel: String) {
    raceList += raceLabel
    addLabel(raceLabel)
  }

  //etc

  }


object Dictionary {

  // some extra information
  val totalPopMass = 6547629
  val stateName = "Massachusetts"

  // returns the Dictionary class given a list of ageLabels and a list of raceLabels 
  def fromParams(ageLabels: Traversable[String], raceLabels: Traversable[String]) = {
    val result = new Dictionary(totalPopMass, stateName)
    for { agelabel <- ageLabels } { result.addAgeLabel(agelabel) }
    for { racelabel <- raceLabels} { result.addRaceLabel(racelabel) }
    result
  }

  // Reads in all of the parameters for a category given the file. Returns a string List (actually, a listBuffer) of each label
  // Eventually, this function will go in LearningComponent
  def readParams(fileName : String) = {
    val source = Source.fromFile(fileName)
    val result = new ListBuffer [String]()
    for {
      line <- source.getLines()
    } {
      // each line is a new label
      result += line 
    }
    result 
  }


  def main(args: Array[String]) = {
    // NOTE: these param files need to be in the format:
    // "white" "black" "asian" "something else" separated by a NEWLINE 
    val ageParams = readParams("data/params/age.txt")
    val raceParams = readParams("data/params/race.txt")
    val dict = Dictionary.fromParams(ageParams, raceParams)

    println("\nTotal number of age parameters: " + dict.ageList.length)
    dict.ageList.foreach(println)

    println("\nTotal number of race parameters: " + dict.raceList.length)
    dict.raceList.foreach(println)

    println("\nAll parameters: " + dict.labels.length)
    dict.labels.foreach(println) 
  }
}
