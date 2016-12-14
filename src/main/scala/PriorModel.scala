import com.cra.figaro.language.{Element, Constant, Flip, Universe}
import com.cra.figaro.library.compound.If
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.library.atomic.discrete.Binomial
import com.cra.figaro.algorithm.ProbQueryAlgorithm
import scala.collection.Map
import scala.collection.mutable.ListBuffer


class PriorParameters(dictionary: Dictionary) {
	val femaleProbability = Beta(1,1)
 	val labelGivenFemaleProbability = dictionary.labels.toList.map(label => (label, Beta(2, 2)))
  val labelGivenMaleProbability = dictionary.labels.toList.map(label => (label, Beta(2, 2)))
	
	val fullParameterList = 
		femaleProbability ::
		labelGivenFemaleProbability.map(pair => pair._2) :::
		labelGivenMaleProbability.map(pair => pair._2)
}

class LearnedParameters(
  val femaleProbability: Double,
  val labelGivenFemaleProbability: Map[String, Double],
  val labelGivenMaleProbability: Map[String, Double]
)

abstract class Model(dictionary: Dictionary) {
  val isFemale: Element[Boolean] 
  val hasLabelElements: List[Any]//List[(String, Element[Boolean])]
}

class LearningModel(dictionary: Dictionary, parameters: PriorParameters) extends Model(dictionary) {
  val isFemale = Flip(parameters.femaleProbability)

  val hasLabelElements = {
    val labelGivenFemaleMap = Map(parameters.labelGivenFemaleProbability:_*)
    val labelGivenMaleMap = Map(parameters.labelGivenMaleProbability:_*)

    val labelsList = dictionary.labels.toList
    for {label <- labelsList} yield {
      val givenFemaleProbability = labelGivenFemaleMap(label)
      val givenMaleProbability = labelGivenMaleMap(label)
      val hasLabelIfFemale = Flip(givenFemaleProbability)
      val hasLabelIfMale = Flip(givenMaleProbability)
      (label, If(isFemale, hasLabelIfFemale, hasLabelIfMale))
    }
  }
}

class ReasoningModel(dictionary: Dictionary, parameters: LearnedParameters) extends Model(dictionary) {
  val isFemale = Flip(parameters.femaleProbability)

  val hasLabelElements = {
    val labelsList = dictionary.labels.toList
    for {label <- labelsList} yield {
      val givenFemaleProbability = parameters.labelGivenFemaleProbability(label)
      val givenMaleProbability = parameters.labelGivenMaleProbability(label)
      val hasLabelIfFemale = Flip(givenFemaleProbability)
      val hasLabelIfMale = Flip(givenMaleProbability)
      (label, If(isFemale, hasLabelIfFemale, hasLabelIfMale))
    }
  }
}




object Model {
  val binomialNumTrials = 20
}



