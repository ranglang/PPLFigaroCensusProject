import com.cra.figaro.language.{Element, Constant, Flip, Universe}
import com.cra.figaro.library.compound.If
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.library.atomic.discrete.Binomial
import com.cra.figaro.algorithm.ProbQueryAlgorithm
import scala.collection.Map
import scala.collection.mutable.ListBuffer


abstract class Model(val dictionary: Dictionary) {
	val metadata: ListBuffer[String]
	val population: Int
	val isFemale: Bool 
 	val ageGroup: String 
 	val race: String
 	val household: String 

}

class PriorParameters(dictionary: Dictionary) {
	// same probability density to male and female 
	val femaleProbability = Beta(1,1)
 	val ageGivenFemaleProbability = Beta(1,1) 	// i think age, race, household should be Select items?
 	val raceGivenFemaleProbability = Beta(1,1)
 	val householdGivenFemaleProbability = Beta(1,1)

	// map the labels in the dictionary
	val labelGivenFemaleProbability = dictionary.labels.map(word => (word, Beta(1,1)))

	val fullParameterList = 
		femaleProbability ::
		ageGivenFemaleProbability :: 
		raceGivenFemaleProbability ::
		householdGivenFemaleProbability :: 
		labelGivenFemaleProbability 
}

class LearnedParameters(
  val femaleProbability: Double,
  val ageGivenFemaleProbability : String,
  val raceGivenFemaleProbability: String,
  val householdGivenFemaleProbability: String, 
  val labelGivenFemaleProbability: Double
)


class LearningModel(dictionary: Dictionary, parameters: PriorParameters) extends Model(dictionary) {
  // well this is the only probabilistic thing we got 
  val isFemale = Flip(parameters.femaleProbability)
  val ageGroup = FLip(parameters.ageGivenFemaleProbability)
  val race = Flip(paramters.raceGivenFemaleProbability)
  val household = Flip(parameters.householdGivenFemaleProbability)

}

class ReasoningModel(dictionary: Dictionary, parameters: LearnedParameters) extends Model(dictionary) {
  val isFemale = Flip(parameters.femaleProbability)
  val ageGroup = FLip(parameters.ageGivenFemaleProbability)
  val race = Flip(paramters.raceGivenFemaleProbability)
  val household = Flip(parameters.householdGivenFemaleProbability)


}

object Model {
  val binomialNumTrials = 20
}
