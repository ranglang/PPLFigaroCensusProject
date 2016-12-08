import com.cra.figaro.patterns.learning._
import com.cra.figaro.algorithm._
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.algorithm.factored._
import com.cra.figaro.algorithm.learning._
import com.cra.figaro.language._
import com.cra.figaro.library.atomic.discrete._
import com.cra.figaro.library.compound.^^
//import com.cra.figaro.library.compound._
//import com.cra.figaro.library.atomic.continuous._
//import com.cra.figaro.language.Universe._
//import com.cra.figaro.util._
//import java.io._
//import scala.math.abs


// The process of using the probability distribution to take into account the evidence is called conditioning on the evidence. The result of conditioning is also a probability distribution and is known as the posterior probability distribution.
//Conditioning on the evidence consists of crossing out possible worlds inconsistent with that evidence and normalizing the remaining probabilities. The probability distribution you get after conditioning on the evidence is called the posterior probability distribution because it comes after seeing the evidence. The process of starting with a prior probability distribution, conditioning on the evidence, and obtaining a posterior probability distribution, is illustrated in figure 4.4.

//undirected dependencies (ch 5)

object InferTest {


	//1. Prior prob distribution
	//2. Observe evidence
	//3. Make posterior. that posterior becomes prior for next piece of evidence

	// beta (ch 3, 5)
	
	val color1 = Flip(0.5)
	val color2 = Flip(0.5)
/*	def constraints() {
		val pair = ^^(color1, color2)

		def sameColorConstraint(pair: (Boolean, Boolean)) = { 
			if (pair._1 == pair._2) 0.3; else 0.1
		}
		val sameColorConstraintValue = Chain(color1, color2, (b1: Boolean, b2: Boolean) => if (b1 == b2) Flip(0.3); else Flip(0.1))

		//underscore means I want the function itself and not to apply it
		pair.setConstraint(sameColorConstraint _)
		//println(sameColorConstraintValue)
	}*/


	def conditions() {
		val sameColorConstraintValue = Chain(color1, color2, (b1: Boolean, b2: Boolean) => if (b1 == b2) Flip(0.3); else Flip(0.1))

		//val color1 = true
		
		//println(sameColorConstraintValue.probability(true))
		//println(sameColorConstraintValue.probability(false))
		sameColorConstraintValue.observe(true)
		println(sameColorConstraintValue.value)

		sameColorConstraintValue.observe(true)
		println(sameColorConstraintValue.value)

		sameColorConstraintValue.observe(false)
		println(sameColorConstraintValue.value)

	}

	def sunny() {
		val sunnyDaysInMonth = Binomial(30, 0.2)
		val monthQuality = Apply(sunnyDaysInMonth, (i:Int) => if (i> 10) "good"; else if (i > 5) "average"; else "poor") 
		val goodMood = Chain(monthQuality, (s:String) =>if (s == "good") Flip (0.9)
			else if (s == "average") Flip (0.6)
			else Flip (0.1))
		println(VariableElimination.probability(goodMood, true))
	}

	def alabama() {
		val totalPopulation = 4779736
		val sunnyDaysInMonth = Binomial(totalPopulation, 0.2)
		val populationOfOneRace = Apply(sunnyDaysInMonth, (i:Int) => if (i> 10) "white alone"; else if (i > 5) "black or african american alone"; else "asian alone")
		val race = Chain(populationOfOneRace, (s:String) => 
			if (s == "white alone") Flip(3275394/totalPopulation)
			else if (s == "black or african american alone") Flip(1251311/totalPopulation)
			else if (s == "american indian and alaska native alone") Flip(28218/totalPopulation)
			else if (s == "asian alone") Flip(53595/totalPopulation) 
			else if (s == "native hawaiian and other pacific islander alone") Flip(3057/totalPopulation)
			else Flip(0.1))

		println(VariableElimination.probability(race, true))


	}

	

	
	def main(args: Array[String]) {
		//constraints()
		//sunny()
		//conditions()
		alabama()
	}
	/*
		Marginal distribution - computing probability dist over a single variable
		Joint distribution over muiltiple variables 
		Need to create a special element to capture the joint behavior of the elements whose joint distribution you want to query
		
		To create a special element ^^
		i.e. val salesPair = ^^(sales(0), sales(1))
		Tuples of up to five arguments 

		To use: VariableElimination.probability(salesPair, (pair: (Double, Double)) => pair._1 < 100 & pair._2 < 100)
	*/



/*
	val whatever = Select(0.9 -> 1000, 0.1 -> 10)
	class Model(val targetPopularity: Double, productQuality: Double, affordability: Double) {
		val numberBuyD = {
			if (targetPopularity > 50) {
				(productQuality * affordability).ceil.toInt
			}
			else (productQuality / affordability).ceil.toInt
		}

		val numberBuy = Select (0.9 -> numberBuyD, 0.1 -> 0)

	}
	

	def predict(targetPopularity: Double, productQuality: Double, affordability: Double) : Double = {
		val model = new Model(targetPopularity, productQuality, affordability)
		val algorithm = Importance(1000, model.numberBuy)
		algorithm.start()
		val result = algorithm.expectation(model.numberBuy, (i:Int) => i.toDouble)
		algorithm.kill()
		result
	}


	def main(args: Array[String]) {
		println(predict(100, 0.5, 0.5))
		println(predict(100, 0.5, 0.9))
		println(predict(100, 0.9, 0.5))
		println(predict(10,  0.9, 0.9))
		println(predict(10,  0.5, 0.5))
	}*/

	/*
	def main(args: Array[String]) {
		val params = ModelParameters()
	 	val xParam = Beta(1, 1)("x", params)
	 	val yGivenXParam = Beta(2, 1)("yGivenX", params)
	 	val yGivenNotXParam = Beta(1, 2)("yGivenNotX", params)
	 	val zGivenYParam = Beta(1, 1)("zGivenY", params)
	 	val zGivenNotYParam = Beta(1, 1)("zGivenNotY", params)
	 	class Model(pc: ParameterCollection) {
	 		val x = Flip(pc.get("x"))
	 		val y = If(x, Flip(pc.get("yGivenX")), Flip(pc.get("yGivenNotX")))
	 		val z = If(y, Flip(pc.get("zGivenY")), Flip(pc.get("zGivenNotY")))
	 	}
		
		for { i <- 1 to 10 } {
	 		val xz = scala.util.Random.nextBoolean()
	 		val model = new Model(params.priorParameters)
	 		model.x.observe(xz)
	 		model.z.observe(xz)
	 	}
	 	

	 	val learningAlg = EMWithVE(10, params)
	 	learningAlg.start()


	 }*/

	/*val gender = Select(0.45-> 'Female, 0.45-> 'Male, 0.1 -> 'Other)
	val isMarried = Flip(0.5)

	//tuple: gender (m/f), isMarried(t/f)
	val theData = Array(('male, true), ('male, true), ('male, true), ('female, true), ('female, false), ('female, false), ('female, false), ('female, false), ('female, false), ('male, false), ('female, false), ('female, false), ('female, false), ('male, true), ('male, true))

	def inferImportance(givenGender: Symbol, givenMarriage: Bool) {
		gender.observe(givenGender)
		isMarried.observe(givenMarriage)
		val alg = Importance(20000, hasChildren)
		alg.start()
		val result = alg.probability(hasChildren, true)
		alg.kill
		println("Probability that a " + givenGender + " with a major in " + givenMajor + " has children: " + result)
	}


	def main(args: Array[String]) {
		println(theData.deep.mkString("\n"))

	}*/


}