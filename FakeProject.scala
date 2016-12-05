import com.cra.figaro.language._
import com.cra.figaro.algorithm._
import com.cra.figaro.algorithm.factored._
import com.cra.figaro.algorithm.sampling._
import com.cra.figaro.library.compound._

/*
Apply takes a number of argument elements and a function. The result of Apply is an element whose value is the given function applied to the values of the arguments. Therefore, Apply defines a dependency from the arguments to the result.
Chain takes an argument element and a function that takes a value of the argument and returns a new element. The result of the Chain is an element whose value is generated by taking the value of the argument, applying the function to get a new element, and generating a value from that element. Therefore, Chain also defines a dependency from the argument to the result.

*/

object FakeProject {

	val x1 = Select(0.1 -> 1, 0.2 -> 2, 0.3 -> 3, 0.4 -> 4)
	val x2 = Flip(0.6)
	val y = RichCPD(x1, x2,
		(OneOf(1, 2), *) -> Flip (0.1),
		(NoneOf(4), OneOf(false)) -> Flip(0.7), 
		(*, *) -> Flip(0.9))

	val sq: Int => Int = x => x * x
	val collegeMajor = Select(0.1->'Psychology, 0.2->'ComputerScience, 0.2->'Biology, 0.1 -> 'Astronomy, 0.3 -> 'Accounting, 0.1 -> 'English)
	val gender = Select(0.45-> 'Female, 0.45-> 'Male, 0.1 -> 'Other)
	val isEmployed = Flip(0.85)

	// CPD = Conditional Probability Distribution
	// RichCPD = better if you have multiple parents

	// bool 
	val hasChildren = RichCPD(collegeMajor, gender, isEmployed, 
		(OneOf('ComputerScience, 'Psychology, 'Astronomy), *, OneOf(true)) -> Flip(0.2), 
		(OneOf('English), *, OneOf(false)) -> Flip(0.8),
		(*, OneOf('Female), *) -> Flip(0.7),
		(*, OneOf('Male), *) -> Flip(0.34),
		(*, *, *) -> Flip(0.001))

	/*val hasChildren2 = If(gender,
		Select(0.6 -> false, 0.4->true),
		Select(0.1 -> false, 0.9 -> true))*/

	def predict() {
		val result = VariableElimination.probability(hasChildren, true)
		println("Probability has children " + result)
	}

	// Scala Symbol type is a unique name for something
	// Useful when you want to create a specific set of values for a variable 
	def inferVE(givenGender: Symbol, givenMajor: Symbol) {

		// BLANK means no prior observations
		if (givenGender != 'BLANK && givenMajor != 'BLANK){
			gender.observe(givenGender)
			collegeMajor.observe(givenMajor)
		}
		val algorithm = VariableElimination(hasChildren)
		algorithm.start()
		val result = VariableElimination.probability(hasChildren, true)
		println("Probability that a " + givenGender + " with a major in " + givenMajor + " has children: " + result)
		println("\tProbability Distribution: " + algorithm.distribution(hasChildren).toList)
		algorithm.kill
	}

	def inferMH(givenGender: Symbol, givenMajor: Symbol) {
		gender.observe(givenGender)
		collegeMajor.observe(givenMajor)
		val alg = MetropolisHastings(20000, ProposalScheme.default, hasChildren)
		alg.start()
		val result = alg.probability(hasChildren, true)
		alg.kill
		println("Probability that a " + givenGender + " with a major in " + givenMajor + " has children: " + result)
	}

	def inferImportance(givenGender: Symbol, givenMajor: Symbol) {
		gender.observe(givenGender)
		collegeMajor.observe(givenMajor)
		val alg = Importance(20000, hasChildren)
		alg.start()
		val result = alg.probability(hasChildren, true)
		alg.kill
		println("Probability that a " + givenGender + " with a major in " + givenMajor + " has children: " + result)
	}

	private class Person {
		val gender = Flip(0.5)
	}


	def main(args: Array[String]) {
			
		/*println("VariableElimination: " + VariableElimination.probability(hasChildren, true))

		val alg = BeliefPropagation(100, hasChildren)
		alg.start()
		println(alg.probability(hasChildren, true))
		//println("BeliefPropagation: " + BeliefPropagation.probability(hasChildren, true))*/

		//println(Uniform(1, 10))



		val x = Flip(0.8)
		val y = Flip(0.6)
		val z = If(x===y, Flip(0.9), Flip(0.1))
		z.observe(false)
		println("\t Exact posterior probability that y is true: " + VariableElimination.probability(y, true))
		val algImportance = Importance(1000, y)
		algImportance.start()
		println("\t Importance with 1000  samples: " + algImportance.probability(y, true))
		algImportance.kill
		val algImportance2 = Importance(2000, y)
		algImportance2.start()
		println("\t Importance with 2000  samples: " + algImportance2.probability(y, true))
		algImportance2.kill
		val algImportance3 = Importance(5000, y)
		algImportance3.start()
		println("\t Importance with 5000  samples: " + algImportance3.probability(y, true))
		algImportance3.kill
		val algImportance4 = Importance(10000, y)
		algImportance4.start()
		println("\t Importance with 10000 samples: " + algImportance4.probability(y, true))
		algImportance4.kill

	/*	println ("\nVariableElimination")
		inferVE('BLANK,  'BLANK)
		inferVE('Female, 'Astronomy)
		inferVE('Male,   'Astronomy)
		inferVE('Female, 'English)
		inferVE('Male,   'English)
		inferVE('Female, 'Biology)
		inferVE('Male,   'Biology)

		println ("\nMetropolisHastings")

		inferMH('Female, 'Astronomy)
		inferMH('Male,   'Astronomy)
		inferMH('Female, 'English)
		inferMH('Male,   'English)
		inferMH('Female, 'Biology)
		inferMH('Male,   'Biology)

		println ("\nImportance")

		inferImportance('Female, 'Astronomy)
		inferImportance('Male,   'Astronomy)
		inferImportance('Female, 'English)
		inferImportance('Male,   'English)
		inferImportance('Female, 'Biology)
		inferImportance('Male,   'Biology)*/

	}
}