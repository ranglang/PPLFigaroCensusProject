import scala.io.Source
import java.io.File
import com.cra.figaro.language.{Constant, Element, Universe}
import com.cra.figaro.algorithm.sampling.Importance
import com.cra.figaro.algorithm.factored.VariableElimination
import com.cra.figaro.algorithm.factored.beliefpropagation.BeliefPropagation

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

  def classify(dictionary: Dictionary, parameters: LearnedParameters, fileName: String) = {
    val file = new File(fileName)
    val email = new Email(file)
    val model = new ReasoningModel(dictionary, parameters)
    email.observeEvidence(model, None, false)
    val algorithm = VariableElimination(model.isSpam)
    algorithm.start()
    val isSpamProbability = algorithm.probability(model.isSpam, true)
    println("Spam probability: " + isSpamProbability)
    algorithm.kill()
    isSpamProbability
  }

  def main(args: Array[String]) {
    val testFileName = "testData.txt"
    val learningFileName = "LearnedParameters.txt"

    val (dictionary, parameters) = loadResults(learningFileName)
    classify(dictionary, parameters, emailFileName)
    println("Done!")
  }
}
