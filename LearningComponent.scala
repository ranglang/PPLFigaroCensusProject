
import java.nio.file.{Files,Paths,Path}
import java.io._
import scala.io.Source
import com.cra.figaro.language.{Universe, Constant}
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.algorithm.learning._
import scala.collection.mutable.ListBuffer
import collection.mutable.HashMap

object LearningComponent {

  // reads dependencies from data file
  // format: 0 283 numLabels "in household" "parent" "white alone"
  //    0 is the gender | 0: male, 1: female 
  //    283 is the population 
  // Each line separated by a newline 
  // returns a hashmap of population and list of metadata labels for each dependency 
  // returns HashMap
  // key:   
  // value: isFemale (the answer)
  /*
    things we wanna store: population (probability)
                           the list of labels
                           isFemale (the answer)

    what I could do is key:   list of the labels/metadata: string
                       value: probability that it is female: Double
                       so  if 1, just population/total
                       and if 0, (1 - population/total)
  */

  def readDependencies(fileName: String, totalPopulation: Int): HashMap[Int, (Boolean, ListBuffer[String])] =
  {
    println("Reading dependencies from " + fileName)
    val source = Source.fromFile(fileName)
    var result: HashMap[Int, (Boolean, ListBuffer[String])] = HashMap()
    for(line <- source.getLines()) {
        val parts = line.split(' ')
        // parts(0) is the gender 
        val numLabels = parts(2).toInt
        val population = parts(1).toInt
        val isFemale = parts(0) == "1"
        val isFemaleProb = isFemale / totalPopulation
        // NOTE: I need to obtain totalPopulation somehow

        // metadata is an array of strings
        val metadata = ListBuffer[String]()
        for (i <- 2 to numLabels) {
            metadata += parts(i)
            metadata += " " // so that all the data separated by spaces
        }

        // Our returning result is a hash table
        // Key: population
        // Values: tuple with isFemale and the metadata
        println("Adding to dependency hashmap: \n")
        println("Key: " + metadata + " Value: " + isFemaleProb) 
        result += metadata, isFemaleProb
    }
    result 
  }


// TODO: Need to make learnMAP work for our situation
  def learnMAP(params: PriorParameters): LearnedParameters = {
    println("Beginning training")
    println("Number of elements: " + Universe.universe.activeElements.length)
    val algorithm = EMWithBP(params.fullParameterList:_*)
    val time0 = System.currentTimeMillis()
    algorithm.start()
    val time1 = System.currentTimeMillis()
    println("Training time: " + ((time1 - time0) / 1000.0))
    val femaleProbability = params.femaleProbability.MAPValue
    val ageGivenFemaleProbability = params.ageGivenFemaleProbability.MAPValue
    val raceGivenFemaleProbability = params.raceGivenFemaleProbability.MAPValue
    val householdGivenFemaleProbability = params.householdGivenFemaleProbability.MAPValue
    val labelGivenFemaleProbability = params.labelGivenFemaleProbability.MAPValue
    algorithm.kill()
    new LearnedParameters(
      femaleProbability,
      ageGivenFemaleProbability,
      raceGivenFemaleProbability,
      householdGivenFemaleProbability, 
      labelGivenFemaleProbability
      
    )
  }

  def saveResults(
      fileName: String,
      dictionary: Dictionary,
      learningResults: LearnedParameters
   ) = {
    val file = new File(fileName)
    val output = new PrintWriter(new BufferedWriter(new FileWriter(file)))

    output.println(dictionary.totalPopulation)
    output.println(learningResults.femaleProbability)
    output.println(learningResults.ageGivenFemaleProbability)
    output.println(learningResults.raceGivenFemaleProbability)
    output.println(learningResults.householdGivenFemaleProbability)
    output.println(learningResults.labelGivenFemaleProbability)


    for {
      label <- dictionary.labels
    } {
      output.println(label)
    }

    output.close()
  }

  def main(args: Array[String]) {
    val stateDataFileName = "data/state_data/25data_gender.json"
    
    // These may need to eventually be there but not now
    //val labelFileName = "labels.txt"
    //val ageLabelFileName = "age.JSON"
    //val raceLabelFileName = "race.JSON"
    val learningFileName = "MYOUTPUT.txt"

    // need to get this data somehow LOLOLOLOL
    val totalPopMass = 6547629
    val stateName = "Massachusetts"

    //var labels = readLables(labelFileName)  // ListBuffer[String]
    //var ageLabels = readLables(ageLabelFileName)
    // var raceLabels = readLables(raceLabelFileName)


    // their emails -- my labels/params 
    // their labels -- my statedata

    val ageParams = readParams("data/params/age.txt")
    val raceParams = readParams("data/params/race.txt")
    var dependencies = readDependencies(stateDataFileName)
    
    val dictionary = Dictionary.fromParams(ageParams, raceParams)
    val params = new PriorParameters(dictionary)

    val models = 
      for { ageLabel <- ageParams }
      yield {
          val model = new LearningModel(dictionary, params)
          //metadata.observeEvidence(model, metadata, true)
          // what should be observing here?
          // TODO 
          
      }

    val results = learnMAP(params)
    saveResults(learningFileName, dictionary, results)
    println("Done!")
  }
}
