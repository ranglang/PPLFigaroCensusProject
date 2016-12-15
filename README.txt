To run Figaro programs that you create:

1) Install SBT v0.13 for your chosen operating system
2) Copy your Figaro program files to FigaroWork\src\main\scala
3) Open a command line prompt
4) Navigate to your local FigaroWork directory
5) At the command prompt, type
	a) sbt "runMain <class_with_main> <parameters>"

Note: don't forget the quotes around the runMain command!

To test if your local environment is properly configured, run

	sbt "runMain Test"

You should see the following output

	[info] Running Test
	1.0


Here is a link to the complete SBT Tutorial:

	http://www.scala-sbt.org/0.13/tutorial/index.html




Once Figaro is installed, follow the following instructions from the main directory.

Note: when including file names, include the whole file path name starting from root

To execute the LearningComponent (which will create the Learned Parameters)
sbt "RunMain LearnedComponent <dataFileName> <labelsFileName> <learningFileName>"
	dataFileName is the file with all of the census data
	labelsFileName is the file with all of the unique labels
	learningFileName is the name of the file where you want to save your learned parameters

To execute the Reasoning Component (which takes in the Learned Parameters)
sbt "runMain ReasoningComponent <testDataFileName> <learnedParametersFileName>"
	testDataFileName is the file with the test data, which is a set of labels, and you are trying to infer the gender of the test data
	learnedParametersFileName is the parameters you learned in the Learning Component