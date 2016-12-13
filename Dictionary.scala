
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import java.nio.file.{Files,Paths,Path}
import java.io._
import scala.io.Source

class Dictionary(totalPopulationState: Int) {
  val statename: String = ""
  val totalPopulation = totalPopulationState

  // do we even need a master list of labels...?
  // prolly not if we have ageList + raceList + etc
  // well i'll just keep one now for funsies!!!!
  val labels: ListBuffer [String] = ListBuffer()
  val dependencies: ListBuffer[String] = ListBuffer()

  val ageList: ListBuffer[String] = ListBuffer()
  val raceList: ListBuffer[String] = ListBuffer()
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
    addLabel(ageLabel)
  }

  def addRaceLabel(raceLabel: String) {
    raceList += raceLabel
    addLabel(raceLabel)
  }
  //etc

  def addDependencies(metadata: String) {
    dependencies += metadata
    // how to add each label in the metadata and make the connections?
  }

  }



// TODO: Make a class for Dependency and for Label
object Dictionary {
  val totalPopMass = 6547629

  // Returns the dictionary with all of the labels..?
  def fromParams(ageLabels: Traversable[String], raceLabels: Traversable[String]) = {
    val result = new Dictionary(totalPopMass)
    for { agelabel <- ageLabels } { result.addAgeLabel(agelabel) }
    for { racelabel <- raceLabels} { result.addRaceLabel(racelabel) }
    result
  }

  // RETURNS a string list
  // i.e. the list of all of the race params 
  // or all of the age params
  // or all of the params in the given file 
  def readParams(fileName : String) = {
    val source = Source.fromFile(fileName)
    val result = new ListBuffer [String]()
    for {
      line <- source.getLines()
    } {
      // each line is a new param label
      result += line 
    }
    result 
  }

  def main(args: Array[String]) = {
    // NOTE: these param files need to be in the format:
    // "white" "black" "asian" "something else" 
    // all on the same line, separated by a NEWLINE 
    val ageParams = readParams("data/params/age.txt")
    val raceParams = readParams("data/params/race.txt")
    val dict = Dictionary.fromParams(ageParams, raceParams)

    println("Total number of age parameters: " + dict.ageList.length)
    dict.ageList.foreach(println)

    println("\nTotal number of race parameters: " + dict.raceList.length)
    dict.raceList.foreach(println)

    println("\nAll parameters: " + dict.labels.length)
    dict.labels.foreach(println) //print all the labels! Woo!
  }

}
