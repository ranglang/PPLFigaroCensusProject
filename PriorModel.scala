import com.cra.figaro.language.{Element, Constant, Flip, Universe}
import com.cra.figaro.library.compound.If
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.library.atomic.discrete.Binomial
import com.cra.figaro.algorithm.ProbQueryAlgorithm
import scala.collection.Map

abstract class Model(val dictionary: Dictionary) {
	val metadata: List[String]
	val population: Int
	val isFemale: Bool 
}

class PriorParameters(dictionary: Dictionary) {
	// same probability density to male and female 
	val femaleProbability = Beta(1,1)

	// map the labels in the dictionary
	val labelGivenFemaleProbability = dictionary.labels.map(word => (word, Beta(1,1)))

	val fullParamterList = 
		femaleProbability ::
		labelGivenFemaleProbability 
}

class LearnedParameters(
  val femaleProbability: Double,
  val labelGivenFemaleProbability: Double
)


class LearningModel(dictionary: Dictionary, parameters: PriorParameters) extends Model(dictionary) {
  // well this is the only probabilistic thing we got 
  val isFemale = Flip(parameters.femaleProbability)

}

class ReasoningModel(dictionary: Dictionary, parameters: LearnedParameters) extends Model(dictionary) {
  val isFemale = Flip(parameters.femaleProbability)

}

object Model {
  val binomialNumTrials = 20
}
