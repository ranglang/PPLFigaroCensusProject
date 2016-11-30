import com.cra.figaro.algorithm.factored._
import com.cra.figaro.language._
import com.cra.figaro.library.compound._
import com.cra.figaro.algorithm.sampling._


object Burglary {
	/*probabilisitc element. parent node*/
	val burglary = Flip(0.01)
	val earthquake = Flip(0.0001)

	/*child node*/
	/*conditional probability distribution*/
	/*CPD - describes its dependency on values of parents*/
	/*probability that alarm going off was caused by burg or earth*/
	val alarm = CPD(burglary, earthquake,
		(false, false) -> Flip(0.001),
		(false, true)  -> Flip (0.1),
		(true, false)  -> Flip (0.9),
		(true, true)   -> Flip(0.99))

	/*child node of alarm*/
	/*conditional probability distribution - if alarm is not going off, the probability that John will call is very low*/
	val johnCalls = CPD(alarm,
		false -> Flip(0.01),
		true  -> Flip(0.7))
	
	def main(args: Array[String]){

		/*given the evidence that John is calling, likelihood that there is a burglary and likelihood that there is an earthquake*/
		johnCalls.observe(true)
		val alg = VariableElimination(burglary, earthquake)

		alg.start()
		alg.probability(burglary, true)
		println("Probability of burglary: " + alg.probability(burglary, true))

		/*terminate algorithm to free up memory*/
		alg.kill

	}
}