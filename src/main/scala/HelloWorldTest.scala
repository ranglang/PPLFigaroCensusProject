import com.cra.figaro.language._
import com.cra.figaro.algorithm._
import com.cra.figaro.algorithm.factored._
import com.cra.figaro.algorithm.sampling._

object HelloWorldTest{
	def main(args: Array[String]) {
		println("Hello world".length)
		println("Hello world".substring(2, 6))
		println("Hello world".replace("H", "3"))

		def addWithDefault(x: Int, y: Int = 5) = x + y
		println(addWithDefault(1, 2)) // => 3
		println(addWithDefault(1))    // => 6

		val sq: Int => Int = x => x * x

		var i = 2
		while (i < 1000) {println ("i " + i); i = sq(i)}

		def sleep(hours: Int) =
    		println(s"I'm sleeping for $hours hours")

     	sleep(100)

     	val myArr = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
     	val myArrSquared = myArr.map(sq)
     	println(myArrSquared.deep.mkString("\n"))
     	println(myArrSquared.filter(_ < 10).deep.mkString("\n"))
     	println(myArrSquared.reduce (_ + _))


     	val aListOfNums = List (1, 2, 3, 4, 10)
     	aListOfNums foreach (x => println(sq(x)))
     	aListOfNums foreach println

		/*Instance of Constant element with probability of 1.0*/
		val helloWorldElement = Select(0.8->"Hello world!", 0.2->"Goodbye world!")

		/*Sample helloWorldElement 1000 times*/	
		val sampleHelloWorld = VariableElimination(helloWorldElement)

		sampleHelloWorld.start()
		println("Probability of Hello world:")
		println(sampleHelloWorld.probability(helloWorldElement, "Hello world!"))
		println("Probability of Goodbye world:")
		println(sampleHelloWorld.probability(helloWorldElement, "Goodbye world!"))
	}
}