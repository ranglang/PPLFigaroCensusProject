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
    val (numEmailsLine :: spamLine :: hasManyUnusualWordsGivenSpamLine :: hasManyUnusualWordsGivenNormalLine ::
        unusualWordGivenManyLine :: unusualWordGivenFewLine :: numWordsLine :: numFeatureWordsLine :: rest) = lines
    val numEmails = numEmailsLine.toInt
    val spamProbability = spamLine.toDouble
    val hasUnusualWordsGivenSpamProbability = hasManyUnusualWordsGivenSpamLine.toDouble
    val hasUnusualWordsGivenNormalProbability = hasManyUnusualWordsGivenNormalLine.toDouble
    val unusualWordGivenHasUnusualProbability = unusualWordGivenManyLine.toDouble
    val unusualWordGivenNotHasUnusualProbability = unusualWordGivenFewLine.toDouble
    val numWords = numWordsLine.toInt
    val numFeatureWords = numFeatureWordsLine.toInt

    var linesRemaining = rest
    var wordsGivenSpamProbabilities = Map[String, Double]()
    var wordsGivenNormalProbabilities = Map[String, Double]()
    var wordsAndCounts = List[(String, Int)]()

    for { i <- 0 until numWords } {
      val word :: countLine :: rest = linesRemaining
      linesRemaining = rest
      wordsAndCounts ::= (word, countLine.toInt)
    }

    for { i <- 0 until numFeatureWords } {
      val word :: givenSpamLine :: givenNormalLine :: rest = linesRemaining
      linesRemaining = rest
      wordsGivenSpamProbabilities += word -> givenSpamLine.toDouble
      wordsGivenNormalProbabilities += word -> givenNormalLine.toDouble
    }

    val dictionary = new Dictionary(numEmails)
    for {
      (word, count) <- wordsAndCounts
      i <- 0 until count
    } {
      dictionary.addWord(word)
    }

    val params = new LearnedParameters(
      spamProbability,
      hasUnusualWordsGivenSpamProbability,
      hasUnusualWordsGivenNormalProbability,
      unusualWordGivenHasUnusualProbability,
      unusualWordGivenNotHasUnusualProbability,
      wordsGivenSpamProbabilities,
      wordsGivenNormalProbabilities
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
    val emailFileName = args(0)
    val learningFileName = args(1)
    val (dictionary, parameters) = loadResults(learningFileName)
    classify(dictionary, parameters, emailFileName)
    println("Done!")
  }
}
