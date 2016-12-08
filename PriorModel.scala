import com.cra.figaro.language.{Element, Constant, Flip, Universe}
import com.cra.figaro.library.compound.If
import com.cra.figaro.library.atomic.continuous.{Beta, AtomicBeta}
import com.cra.figaro.library.atomic.discrete.Binomial
import com.cra.figaro.algorithm.ProbQueryAlgorithm
import scala.collection.Map



class LearnedParameters(
	val race: String,
	val householdBySize: String,
	val householdByRelationship: String,
	val householdByChildren: String,
	val ageGroups: String,
	val livingAlone: Boolean,
	val sexByAge: String,
	val state: String) 


class LearnedParameters(
  val spamProbability: Double,
  val hasManyUnusualWordsGivenSpamProbability: Double,
  val hasManyUnusualWordsGivenNormalProbability: Double,
  val unusualWordGivenManyProbability: Double,
  val unusualWordGivenFewProbability: Double,
  val wordGivenSpamProbabilities: Map[String, Double],
  val wordGivenNormalProbabilities: Map[String, Double]
)