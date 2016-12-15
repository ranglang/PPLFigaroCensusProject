import com.cra.figaro.language.{Element, Constant, Flip, Universe}
import com.cra.figaro.library.compound.If
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.library.atomic.discrete.Binomial
import com.cra.figaro.algorithm.ProbQueryAlgorithm
import scala.collection.Map
import scala.collection.mutable.ListBuffer


/*
 *  PriorParameters: parameters before we train. We do not know anything about our data yet
 */

class PriorParameters(dictionary: Dictionary) {
	val femaleProbability = Beta(1,1)
 	val labelGivenFemaleProbability = dictionary.labels.toList.map(label => (label, Beta(2, 2)))
  val labelGivenMaleProbability = dictionary.labels.toList.map(label => (label, Beta(2, 2)))
	
	val fullParameterList = 
		femaleProbability ::
		labelGivenFemaleProbability.map(pair => pair._2) :::
		labelGivenMaleProbability.map(pair => pair._2)
}


/*
 *  LearnedParameters: parameters before we test. These parameters are learned from training.
 */

class LearnedParameters(
  val femaleProbability: Double,
  val labelGivenFemaleProbability: Map[String, Double],
  val labelGivenMaleProbability: Map[String, Double]
)


/*
 *  Model encompasses the following elements:
 *        isFemale, which is our Flip element

 *        hasLabelElements, which should have type 
 *        List[(String, Element[Boolean])]
 *        However, we were unable to do that b/c of strange compiler errors
 */
abstract class Model(dictionary: Dictionary) {
  val isFemale: Element[Boolean] 
  val hasLabelElements: List[Any]
}


/*
 *  LearningModel uses PriorParameters consisting of Beta elements for each
 *  parameter.
 */
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


/*
 *  LearningModel uses LearnedParameters, which are learned through training
 */
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



