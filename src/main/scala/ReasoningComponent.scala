import scala.io.Source
import java.io.File
import com.cra.figaro.language.{Constant, Element, Universe}
import com.cra.figaro.algorithm.sampling.Importance
import com.cra.figaro.algorithm.factored.VariableElimination
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.algorithm.factored.beliefpropagation.BeliefPropagation

import java.nio.charset.CodingErrorAction
import scala.io.Codec
import scala.collection.mutable.ListBuffer


/*
 * Reasoning Component of our model.
 * This is where we used the trained model to reason about test data
 */

object ReasoningComponent {


  /*
      loadResults takes in the fileName of the learned parameters
      We parse the file and add the elements to our dictionary as necessary.
   */
  def loadResults(fileName: String) = {
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    val source = Source.fromFile(fileName)
    val lines = source.getLines().toList
    val (femaleLine :: numLabelsLine :: rest) = lines

    val femaleProbability = femaleLine.toDouble
    val numLabels = numLabelsLine.toInt 

    var linesRemaining = rest 
    var labelsGivenFemaleProbabilities = Map[String, Double] ()
    var labelsGivenMaleProbabilities = Map[String, Double] ()

    val dictionary = new Dictionary()
    for { i <- 0 until numLabels } {
      val label :: givenFemaleLine :: givenMaleLine :: rest = linesRemaining 
      linesRemaining = rest 
      dictionary.addLabel(label)
      labelsGivenFemaleProbabilities += label -> givenFemaleLine.toDouble
      labelsGivenMaleProbabilities += label -> givenMaleLine.toDouble 

    }

    /* these are the parameters that were learned from the training data */
    val params = new LearnedParameters(
      femaleProbability,
      labelsGivenFemaleProbabilities,
      labelsGivenMaleProbabilities
      )
    
    (dictionary, params)
  }


  /* observe the evidence */
  def observeEvidence(model: Model, learning: Boolean, data: ListBuffer[String]) = {
      for {
        (label: String, element: Element[Boolean]) <- model.hasLabelElements
        }{
 //         println("Observing: " + element) 
          element.observe(data.toList.contains(label))
        }
  }


  /*
      classify runs the variable elimination algorithm on the isFemale variable
   */
  def classify(dictionary: Dictionary, parameters: LearnedParameters, fileName: String) = {
    val file = new File(fileName)
    val source = Source.fromFile(fileName)
    val labels = new ListBuffer [String]()
    for {
      line <- source.getLines()
    } {
      // each line is a new label
      labels += line 
 //     println("New label: " + line)
    }

    val model = new ReasoningModel(dictionary, parameters)
    observeEvidence(model, false, labels)

    val algorithm = VariableElimination(model.isFemale)
    
    //val algorithm = Importance(5000, model.isFemale)


    algorithm.start()
    val isFemaleProbability = algorithm.probability(model.isFemale, true)
    println("Female probability: " + isFemaleProbability)
    algorithm.kill()
    isFemaleProbability
  }


  /*
      NOTE: right now the text files are hard coded. 
      Eventually the files will be read in from the console line.    
   */
  def main(args: Array[String]) {
   // val testFileName = "testData.txt"
   // val learningFileName = "LearnedParametersSuperBaby.txt"

    val testFileName = args(0)
    val learningFileName = args(1)

    val (dictionary, parameters) = loadResults(learningFileName)
    classify(dictionary, parameters, testFileName)
    println("Done!")
  }
}
