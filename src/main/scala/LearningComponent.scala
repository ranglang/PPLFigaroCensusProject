
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

    returning a list of tuples
    (isFemale, population, metadata)
  */


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



  def readDependencies(fileName: String, totalPopulation: Int): ListBuffer[(Boolean, Int, ListBuffer[String])] =
  {
    println("Reading dependencies from " + fileName)
    val source = Source.fromFile(fileName)
    var result: ListBuffer[(Boolean, Int, ListBuffer[String])] = ListBuffer()
    for(line <- source.getLines()) {
        val parts = line.split(',')
        // parts(0) is the gender 
        val numLabels = parts(2).toInt
        val population = parts(1).toInt
        val isFemale = parts(0) == "1"
        //val isFemaleProb = isFemale / totalPopulation
        // NOTE: I need to obtain totalPopulation somehow

        // metadata is an array of strings
        val metadata = ListBuffer[String]()
        for (i <- 1 to numLabels) {
            metadata += parts(i+2)
        }

        // Our returning result is a hash table
        // Key: population
        // Values: tuple with isFemale and the metadata
 //       println("\nAdding to dependency list: ")
  //      println("isFemale: " + isFemale + " population " + population + " metadata " + metadata) 
        val newDependencyData = (isFemale, population, metadata)
        result += newDependencyData
    }
    result 
  }


// TODO: Need to make learnMAP work for our situation
  /*def learnMAP(params: PriorParameters): LearnedParameters = {
    

    println("Beginning training")

    
    println("Number of elements: " + Universe.universe.activeElements.length)
    val algorithm = EMWithBP(params.fullParameterList:_*)
    val time0 = System.currentTimeMillis()
    algorithm.start()
    val time1 = System.currentTimeMillis()
    println("Training time: " + ((time1 - time0) / 1000.0))
    val femaleProbability = params.femaleProbability.MAPValue
    val ageGivenFemaleProbability = params.ageGivenFemaleProbability.MAPValue
    val ageGivenMaleProbability = params.ageGivenMaleProbability.MAPValue
    val raceGivenFemaleProbability = params.raceGivenFemaleProbability.MAPValue
    val raceGivenMaleProbability = params.raceGivenMaleProbability.MAPValue

    algorithm.kill()
    new LearnedParameters(
      femaleProbability,
      ageGivenFemaleProbability,
      ageGivenMaleProbability,
      raceGivenFemaleProbability,
      raceGivenMaleProbability
    )

  }*/

  /*def saveResults(
      fileName: String,
      dictionary: Dictionary,
      learningResults: LearnedParameters
   ) = {
    val file = new File(fileName)
    val output = new PrintWriter(new BufferedWriter(new FileWriter(file)))

    output.println(dictionary.totalPopulation)
    output.println(learningResults.femaleProbability)
    output.println(learningResults.ageGivenFemaleProbability)
    output.println(learningResults.ageGivenMaleProbability)
    output.println(learningResults.raceGivenFemaleProbability)
    output.println(learningResults.raceGivenMaleProbability)


    for {
      label <- dictionary.labels
    } {
      output.println(label)
    }

    output.close()
  }*/

  def main(args: Array[String]) {
    val stateDataFileName = "data/formatted_data.txt"
    val learningFileName = "MYOUTPUT.txt"
    val totalPopMass = 6547629
    val stateName = "Massachusetts"
    val ageParams = readParams("data/params/age.txt")
    val raceParams = readParams("data/params/race.txt")
    var dependencies = readDependencies(stateDataFileName, totalPopMass)
    
    val dictionary = Dictionary.fromParams(ageParams, raceParams)
    val params = new PriorParameters(dictionary)

    def observeEvidence(model: Model, isFemale: Option[Boolean], metadata: ListBuffer[String], learning: Boolean, data: Dictionary) = {
        isFemale match {
            case Some(b) => model.isFemale.observe(b)
            case None => ()
        }


        for {
          (label, element) <- model.hasLabelElements
          }{
            element.observe(dictionary.labels.contains(label))
          }
    }
}


/*
// DON't NEED POPULATION ... get rid of it later
    val models = 
      for {(isFemale, population, metadata) <- dependencies }
      yield {
          val model = new LearningModel(dictionary, params)
          var i = 0
          for (i <- population) {
            observeEvidence(model, isFemale, metadata, true, dictionary)
          }
          model 
      }

    //val results = learnMAP(params)
    //saveResults(learningFileName, dictionary, results)*/
    println("Done!")
  
}
