import scala.io.Source
import java.io.File
import com.cra.figaro.language.{Constant, Element, Universe}
import com.cra.figaro.algorithm.sampling.Importance
import com.cra.figaro.algorithm.factored.VariableElimination
import com.cra.figaro.algorithm.factored.beliefpropagation.BeliefPropagation

import java.nio.charset.CodingErrorAction
import scala.io.Codec

object ReasoningComponent {
  def loadResults(fileName: String) = {
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    val source = Source.fromFile(fileName)
    val lines = source.getLines().toList
    val (femaleLine :: numLablesLine :: rest)

    val femaleProbability = femaleLine.toDouble
    val numLabels = numLabelsLine.toInt 

    var linesRemaining = rest 
    var labelsGivenFemaleProbabilities = Map[String, Double] ()
    var labelsGivenMaleProbabilities = Map[String, Double] ()

    val dictionary = new Dictionary()
    for { i <- 0 until numLabels } {
      val label :: givenFemaleLine :: givenMaleLine :: rest = linesRemaining 
      linesRemaining = rest 
      dictionary.addLabel(word)
      labelsGivenFemaleProbabilities += label -> givenFemaleLine.toDouble
      labelsGivenMaleProbabilities += label -> givenMaleLine.toDouble 

    }


    val params = new LearnedParameters(
      femaleProbability,
      labelsGivenFemaleProbabilities,
      labelsGivenMaleProbabilities
      )
    
    (dictionary, params)
  }

  def observeEvidence(model: Model, learning: Boolean, data: ListBuffer[String]) = {
      for {
        (label: String, element: Element[Boolean]) <- model.hasLabelElements
        }{
          element.observe(data.labels.contains(label))
        }
  }

  def classify(dictionary: Dictionary, parameters: LearnedParameters, fileName: String) = {
    val file = new File(fileName)
    val source = Source.fromFile(fileName)
    val labels = new ListBuffer [String]()
    for {
      line <- source.getLines()
    } {
      // each line is a new label
      labels += line 
    }

    val model = new ReasoningModel(dictionary, parameters)
    observeEvidence(model, False, labels)

    val algorithm = VariableElimination(model.isFemale)
    algorithm.start()
    val isFemaleProbability = algorithm.probability(model.isFemale, true)
    println("Female probability: " + isFemaleProbability)
    algorithm.kill()
    isFemaleProbability
  }

  def main(args: Array[String]) {
    val testFileName = "testData.txt"
    val learningFileName = "LearnedParameters.txt"

    val (dictionary, parameters) = loadResults(learningFileName)
    classify(dictionary, parameters, emailFileName)
    println("Done!")
  }
}
