
import java.nio.file.{Files,Paths,Path}
import java.io._
import scala.io.Source
import com.cra.figaro.language.{Universe, Constant}
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.algorithm.learning._
import scala.collection.mutable.ListBuffer

object LearningComponent {

  // reads labels from labels.txt
  // labels are in format "label1" "label2"
  // note: each label is on its own line...(for getline to work)
  def readLables(fileName: String) : ListBuffer[String]
  {
    println("Reading labels from " + filename)
    val source = Source.fromFile(fileName)
    val result = new ListBuffer[String]()

    for {line <- source.getLines()} {
        result += line 
    }
    result 
  }

  // reads dependencies from data file
  // format: 283 numLabels "skfljkfjslkdj" "slkdjfklsd" "sdkfjlkd"
  // returns a hashmap of population and list of metadata labels for each dependency 
  def readDependencies(fileName: String): Map[Int, ListBuffer[String]]
  {
    println("Reading dependencies from " + fileName)
    val source = Source.fromFile(fileName)
    var result: Map[Int, ListBuffer[String]] = Map()
    while(line <- source.getLines()) {
        val parts = line.split(' ')
        val numLabels = parts(1)
        val population = parts(0)
        val metadata = ListBuffer[Int]()
        for (val i = 2; i < numLables; i++) {
            metadata += parts(i)
        }
        result += population -> metadata
    }
    result //WHERE DO I SAVE THE DEPENDENCIES???!?!??!?!??!?!?!?!?!?!?!?!?
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
    val labelGivenFemaleProbability = params.labelGivenFemaleProbability.MAPValue
    val ageGivenFemaleProbability = params.ageGivenFemaleProbability.MAPValue
    algorithm.kill()
    new LearnedParameters(
      femaleProbability,
      labelGivenFemaleProbability
      ageGivenFemaleProbability
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

    for {
      label <- dictionary.labels
    } {
      output.println(label)
    }

    output.close()
  }

  def main(args: Array[String]) {
    val dependenciesFileName = "25data_gender.json"
    val labelFileName = "labels.txt"
    val learningFileName = args(2)

    val labels = readLables(labelFileName)
    val dependencies = readDependencies(dependenciesFileName)
    val dictionary = Dictionary.fromLabels(labels.values)

    val params = new PriorParameters(dictionary)
    val models = 
      for {(dependency) <- dependencies}
      yield {
          val model = new LearningModel(dictionary, params)
          dependency.observeEvidence(model, *fileLineNum?*, true)
      }

    val results = learnMAP(params)
    saveResults(learningFileName, dictionary, results)
    println("Done!")
  }
}
