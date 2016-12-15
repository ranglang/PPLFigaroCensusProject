
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import java.nio.file.{Files,Paths,Path}
import java.io._
import scala.io.Source

/*
 * The Dictionary class holds all of the labels by category
 * At the moment, it only holds a list of labels for age and race, but the
 * hope is that we will eventually have all of the different categories
 * covered.
 *
 * We also have a master list, called labels, with ALL of the labels.
 * 
 * In LearningComponent we will read in the data file and associate
 * the different labels with gender - "isFemale"
*/

class Dictionary() {
  val labels: ListBuffer [String] = ListBuffer()

  def addLabel(labelName: String) 
  {
      labels += labelName
  }

}


object Dictionary {

  /*
      fromParams takes in a list of labels and adds them to the labels
      list in a new dictionary
  */
  def fromParams(labelsList: Traversable[String]) = {
      val result = new Dictionary()
      for { label <- labelsList } {result.addLabel(label)}
      result
  }



  /*
      Console arguments include the file which contains all of the unique labels.
      labels.txt should be in the follownig format:
          label1 
          label2
          label3
      Each label is separated by a newline.
  */
  def main(args: Array[String]) = {
    val labelsParams = LearningComponent.readParams("data/params/labels.txt")
    val dict = Dictionary.fromParams(labelsParams)

    // a print statement just for testing purposes 
    println("\nAll parameters: " + dict.labels.length)
    dict.labels.foreach(println) 
  }
}
