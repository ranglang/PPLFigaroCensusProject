
import java.nio.file.{Files,Paths,Path}
import java.io._
import scala.io.Source
import com.cra.figaro.language.{Universe, Constant, Element}
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.algorithm.learning._
import scala.collection.mutable.ListBuffer
import collection.mutable.HashMap

object LearningComponent {

  /*
    readParams reads in a text file with all of the labels
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


  /*
    readDependencies takes in the fileName for the data file

    Format of data file: isFemale population numLabels "in household" "parent" "white alone"
        isFemale is the gender (0 or 1)
            0 -> male
            1 -> female
        population: Int
        numLabels:  Int

    Returns a list of tuples of the form:
        (isFemale, population, metadata)
    where metadata is a list of labels, where each label is represented by a string

  */
  def readDependencies(fileName: String): ListBuffer[(Boolean, Int, ListBuffer[String])] =
  {
    println("Reading dependencies from " + fileName)
    val source = Source.fromFile(fileName)
    var result: ListBuffer[(Boolean, Int, ListBuffer[String])] = ListBuffer()
    for(line <- source.getLines()) {
        val parts = line.split(',')
        val numLabels = parts(2).toInt
        val population = parts(1).toInt
        val isFemale = parts(0) == "1"
       
        // metadata is an array of strings
        val metadata = ListBuffer[String]()
        for (i <- 1 to numLabels) {
            metadata += parts(i+2)
        } 
        val newDependencyData = (isFemale, population, metadata)
        result += newDependencyData
    }
    result 
  }


def learnMAP(params: PriorParameters): LearnedParameters = {
    
    println("Beginning training")    
    println("Number of elements: " + Universe.universe.activeElements.length)
    val algorithm = EMWithBP(params.fullParameterList:_*)
    val time0 = System.currentTimeMillis()
    algorithm.start()
    val time1 = System.currentTimeMillis()
    println("Training time: " + ((time1 - time0) / 1000.0))
    val femaleProbability = params.femaleProbability.MAPValue
    val labelGivenFemaleProbability = 
      for {(word, param) <- params.labelGivenFemaleProbability} 
      yield (word, param.MAPValue)
    val labelGivenMaleProbability = 
      for {(word, param) <- params.labelGivenMaleProbability } 
      yield (word, param.MAPValue)
    algorithm.kill()

    new LearnedParameters(
      femaleProbability,
      labelGivenFemaleProbability.toMap,
      labelGivenMaleProbability.toMap
    )
  }

  def saveResults(
      fileName: String,
      dictionary: Dictionary,
      learningResults: LearnedParameters
   ) = {
    val file = new File(fileName)
    val output = new PrintWriter(new BufferedWriter(new FileWriter(file)))

    
    output.println(learningResults.femaleProbability)
    output.println(learningResults.labelGivenFemaleProbability)
    output.println(learningResults.labelGivenMaleProbability)

    for {
      label <- dictionary.labels.toList
    } {
      output.println(label)
    }

    output.close()
  }


  /*
    observes the evidence, of course
  */
  def observeEvidence(model: Model, isFemaleVal: Boolean, metadata: ListBuffer[String], learning: Boolean, data: Dictionary) = {

      model.isFemale.observe(isFemaleVal)

        for {
          (label: String, element: Element[Boolean]) <- model.hasLabelElements
          }{
            element.observe(data.labels.contains(label))
          }
    }



/*
    NOTE: Our text files are hardcoded in at the moment
    Later, we will change them so that we read in the text files from the arguments
*/
  def main(args: Array[String]) {
    val stateDataFileName = "data/mini_baby_data.txt"
    val learningFileName = "MYOUTPUT.txt"
    val ageParams = readParams("data/params/age.txt")
    val raceParams = readParams("data/params/race.txt")
    var dependencies = readDependencies(stateDataFileName)
    
    val dictionary = Dictionary.fromParams(ageParams, raceParams)
    val params = new PriorParameters(dictionary)

    val models = 
      for {(isFemale, population, metadata) <- dependencies }
      yield {
          val model = new LearningModel(dictionary, params)
          println("\nAbout to start observing " + population + " people......")
          for (i <- 1 to population) {
            observeEvidence(model, isFemale, metadata, true, dictionary)
          }
          println("\nDone observing! Yay!!! wee")
          model 
      }

      val results = learnMAP(params)
      saveResults(learningFileName, dictionary, results)

    println("Done!")
  }
}
