import com.cra.figaro.patterns.learning._
import com.cra.figaro.algorithm._
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.algorithm.factored._
import com.cra.figaro.algorithm.learning._
import com.cra.figaro.language._
import com.cra.figaro.library.atomic.discrete._
//import com.cra.figaro.library.compound._
//import com.cra.figaro.library.atomic.continuous._
//import com.cra.figaro.language.Universe._
//import com.cra.figaro.util._
//import java.io._
//import scala.math.abs

object InferTest {

	val whatever = Select(0.9 -> 1000, 0.1 -> 10)
	class Model(val targetPopularity: Double, productQuality: Double, affordability: Double) {
		val numberBuyD = {
			if (targetPopularity > 50) {
				(productQuality * affordability).ceil.toInt
			}
			else (productQuality / affordability).ceil.toInt
		}

		val numberBuy = Select (0.9 -> numberBuyD, 0.1 -> 0)
		/*def generateLikes(numFriends: Int, productQuality: Double) : Element[Int] = {
			def helper(friendsVisited: Int, totalLikes: Int, unprocessedLikes: Int): Element[Int] = {
				if (unprocessedLikes == 0) Constant (totalLikes)
				else {
					val unvisitedFraction = 1.0 - (friendsVisited.toDouble - 1) / (numFriends - 1)
					val newlyVisited = Binomial(2, unvisitedFraction)
					val newlyLikes = Binomial(newlyVisited, Constant(productQuality))
					Chain(newlyVisited, newlyLikes, (visited: Int, likes: Int) => helper (friendsVisited + 1000, totalLikes + likes, unprocessedLikes + likes - 1))
				}
			}
			helper(1, 1, 1)
		}
		val targetSocialNetwork = new */

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
	}

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