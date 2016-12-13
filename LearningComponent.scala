/*
import java.nio.file.{Files,Paths,Path}
import java.io._
import scala.io.Source
import com.cra.figaro.language.{Universe, Constant}
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.algorithm.learning._
import scala.collection.mutable.ListBuffer
import collection.mutable.HashMap

object LearningComponent {

  // reads labels from labels.txt
  // labels are in format "label1" "label2"
  // note: each label is on its own line...(for getline to work)
  def readLables(fileName: String) : ListBuffer[String] = {
    println("Reading labels from " + fileName)
    val source = Source.fromFile(fileName)
    val result = new ListBuffer[String]()

    for {line <- source.getLines()} {
        result += line 
    }
    result 
  }



  // reads dependencies from data file
  // format: 0 283 numLabels "skfljkfjslkdj" "slkdjfklsd" "sdkfjlkd"
  // 0: male 1:female 
  // 283: population 
  // returns a hashmap of population and list of metadata labels for each dependency 
  def readDependencies(fileName: String): HashMap[Int, (Boolean, ListBuffer[String])] =
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

        // metadata is an array of strings
        val metadata = ListBuffer[String]()
        for (i <- 2 to numLabels) {
            metadata += parts(i)
        }

        // Our returning result is a hash table
        // Key: population
        // Values: tuple with isFemale and the metadata 
        result += population -> (isFemale, metadata)
    }
    result //WHERE DO I SAVE THE DEPENDENCIES???!?!??!?!??!?!?!?!?!?!?!?!?
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
    val stateDataFileName = "25data_gender.json"
    //val labelFileName = "labels.txt"
    val ageLabelFileName = "age.JSON"
    val raceLabelFileName = "race.JSON"
    val learningFileName = "MYOUTPUT.txt"

    //var labels = readLables(labelFileName)  // ListBuffer[String]
    var ageLabels = readLables(ageLabelFileName)
    var raceLabels = readLables(raceLabelFileName)



    var dependencies = readDependencies(dependenciesFileName)
    val dictionary = Dictionary.fromData(dependencies, labels)

    val params = new PriorParameters(dictionary)
    val models = 
      for { (population, (isFemale, metadata)) <- dependencies }
      yield {
          val model = new LearningModel(dictionary, params)
          //metadata.observeEvidence(model, metadata, true)
          // what should be observing here?
      }

    val results = learnMAP(params)
    saveResults(learningFileName, dictionary, results)
    println("Done!")
  }
}
*/