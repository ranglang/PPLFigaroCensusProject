

import com.cra.figaro.language.{Element, Constant, Flip, Universe}
import com.cra.figaro.library.compound.If
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.library.atomic.discrete.Binomial
import com.cra.figaro.algorithm.ProbQueryAlgorithm
import scala.collection.Map
import scala.collection.mutable.ListBuffer


abstract class Model(val dictionary: Dictionary) {
//	val metadata: ListBuffer[String]
//	val population: Int
	val isFemale: Element[Boolean] 
  val hasLabelElements: List[(String, Element[Boolean])]
 	///val ageGroup: List[(String, Element[Boolean])]
 	//val raceGroup: List[(String, Element[Boolean])]

 	// add household if time 


}

class PriorParameters(dictionary: Dictionary) {
	// same probability density to male and female 
	val femaleProbability = Beta(1,1)
 	val ageGivenFemaleProbability = dictionary.ageList.map(age => (age, Beta(2, 2)))
	val ageGivenMaleProbability   = dictionary.ageList.map(age => (age, Beta(2, 2)))
 	val raceGivenFemaleProbability = dictionary.raceList.map(race => (race, Beta(2, 2)))
	val raceGivenMaleProbability   = dictionary.raceList.map(race => (race, Beta(2, 2)))
 	
	// For now, only dealing with age and race  
	// Can put in household later if time permitting

	//val labelGivenFemaleProbability = Beta(1,1)//dictionary.labels.map(word => (word, Beta(1,1)))

	/*val fullParameterList = 
		femaleProbability ::
		ageGivenFemaleProbability.map(pair => pair._2) :::
		ageGivenMaleProbability.map(pair => pair._2) :::
		raceGivenFemaleProbability.map(pair => pair._2) :::
		raceGivenMaleProbability.map(pair => pair._2)*/
}

class LearnedParameters(
  val femaleProbability: Double,
  val ageGivenFemaleProbability : Map[String, Double],
  val ageGivenMaleProbability: Map[String, Double],
  val raceGivenFemaleProbability: Map[String, Double],
  val raceGivenMaleProbability: Map[String, Double]
)


class LearningModel(dictionary: Dictionary, parameters: PriorParameters) extends Model(dictionary) {
  // well this is the only probabilistic thing we got 
  val isFemale = Flip(parameters.femaleProbability)


  val hasLabelElements = {
    val labelGivenFemaleMap = Map(parameters.ageGivenFemaleProbability:_*)
    val labelGivenMaleMap = Map(parameters.ageGivenMaleProbability:_*)

    for {label <- dictionary.labels} yield {
      val givenFemaleProbability = labelGivenFemaleMap(label)
      val givenMaleProbability = labelGivenMaleMap(label)
      val hasLabelIfFemale = Flip(givenFemaleProbability)
      val hasLabelIfMale = Flip(givenMaleProbability)
      (label, If(isFemale, hasLabelIfFemale, hasLabelIfMale))
      }
  }

  // outputs List[(String, Element[Boolean])]
  /*val ageGroup = {
  	val labelGivenFemaleMap = Map(parameters.ageGivenFemaleProbability:_*)
  	val labelGivenMaleMap = Map(parameters.ageGivenMaleProbability:_*)
  	// for each age label in the ageList, assign a probability for each if female or if male 

  	for {ageLabel <- dictionary.ageList} yield {
  		val givenFemaleProbability = labelGivenFemaleMap(ageLabel)
  		val givenMaleProbability = labelGivenMaleMap(ageLabel)
  		val isThisAgeIfFemale = Flip(givenFemaleProbability)
  		val isThisAgeIfMale = Flip(givenMaleProbability)
  		(ageLabel, If(isFemale, isThisAgeIfFemale, isThisAgeIfMale))
  		}
  	}


  	val raceGroup = {
  		val labelGivenFemaleMap = Map(parameters.raceGivenFemaleProbability:_*)
  		val labelGivenMaleMap = Map(parameters.raceGivenMaleProbability:_*)
  		for {raceLabel <- dictionary.raceList} yield {
  			val givenFemaleProbability = labelGivenFemaleMap(raceLabel)
  			val givenMaleProbability = labelGivenMaleMap(raceLabel)
  			val isThisRaceIfFemale = Flip(givenFemaleProbability)
  			val isThisRaceIfMale = Flip(givenMaleProbability)
  			(raceLabel, If(isFemale, isThisRaceIfFemale, isThisRaceIfMale))
  		}
  	}*/
}

/*class ReasoningModel(dictionary: Dictionary, parameters: LearnedParameters) extends Model(dictionary) {
  val isFemale = Flip(parameters.femaleProbability)

  val ageGroup = {
  	for {ageLabel <- dictionary.ageList} yield {
  		val givenFemaleProbability = parameters.ageGivenFemaleProbability(ageLabel)
  		val givenMaleProbability = parameters.ageGivenMaleProbability(ageLabel)
  		val isThisAgeIfFemale = Flip(givenFemaleProbability)
  		val isThisAgeIfMale = Flip(givenMaleProbability)
  		(ageLabel, If(isFemale, isThisAgeIfFemale, isThisAgeIfMale))
  	}
  }

  val raceGroup = {
  	for {raceLabel <- dictionary.raceList} yield {
  		val givenFemaleProbability = parameters.raceGivenFemaleProbability(raceLabel)
  		val givenMaleProbability = parameters.raceGivenMaleProbability(raceLabel)
  		val isThisRaceIfFemale = Flip(givenFemaleProbability)
  		val isThisRaceIfMale = Flip(givenMaleProbability)
  		(raceLabel, If(isFemale, isThisRaceIfFemale, isThisRaceIfMale))
  	}
  }
}
*/
object Model {
  val binomialNumTrials = 20
}



