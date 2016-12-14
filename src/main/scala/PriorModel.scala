

import com.cra.figaro.language.{Element, Constant, Flip, Universe}
import com.cra.figaro.library.compound.If
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.library.atomic.discrete.Binomial
import com.cra.figaro.algorithm.ProbQueryAlgorithm
import scala.collection.Map
import scala.collection.mutable.ListBuffer




class PriorParameters(dictionary: Dictionary) {
	// same probability density to male and female 
	val femaleProbability = Beta(1,1)
 	val labelGivenFemaleProbability = dictionary.labels.toList.map(label => (label, Beta(2, 2)))
  val labelGivenMaleProbability = dictionary.labels.toList.map(label => (label, Beta(2, 2)))
	
	//val labelGivenFemaleProbability = Beta(1,1)//dictionary.labels.map(word => (word, Beta(1,1)))

	val fullParameterList = 
		femaleProbability ::
		labelGivenFemaleProbability.map(pair => pair._2) :::
		labelGivenMaleProbability.map(pair => pair._2)
}

class LearnedParameters(
  val femaleProbability: Double,
  val labelGivenFemaleProbability: Map[String, Double],
  val labelGivenMaleProbability: Map[String, Double]
  // val ageGivenFemaleProbability : Map[String, Double],
  // val ageGivenMaleProbability: Map[String, Double],
  // val raceGivenFemaleProbability: Map[String, Double],
  // val raceGivenMaleProbability: Map[String, Double]
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
      val myAnswer = Flip(0.5)
      //("label", myAnswer)
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



