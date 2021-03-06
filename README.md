# BEAST2 Language Generation

This repository is home to the BEAST2 language generation plugin, which, similar to the `seqgen` (for DNA) module in BEAST2, generates synthetic language data.

## Quick Start 

Simply import the project into Eclipse, along with a copy of the [BEAST2 project](https://github.com/CompEvol/beast2), and [BeastLabs project](https://github.com/BEAST2-Dev/BEASTLabs). An ant `build.xml` file is provided to produce the required Jar file: `LanguageSequenceGen.jar`.

### Command Line

Like the original `seqgen`, this plugin uses the same format for command line runs:

```
java LanguageSequenceGenInterface -input <beast file> -meaningClasses <nr of instantiations> [-output <output file>]
```
* The `<beast file>` is an `xml` file that specifics the initial input parameters. An example is provided below. 

* To determine the number of meaning classes, `<nr of instantiations>` is provided, the position of first cognate in each meaning class is provided as an additional sequence at the end. 

* If an `<output file>` is not provided, the output will be written to `std.out`. 

### BeastBorrowingPluginTest

Like most BEAST2 plugins, this plugin has its own testing suite defined in `BeastBorrowingPluginTest`. In this class, the `SeqGenTest()` runs the plugin using arguments defined within the function: 

```Java
private static void SeqGenTest() {
		String[] args = {"examples/testSeqLangGen.xml","2","examples/output.xml"};
		LanguageSequenceGen.main(args);
}
```

The format of the arguments are the same as those in the *Command Line* section. 

There are a number of other tests in the class that produce `csv` files, which are in turn used to validate various portions of the plugin in R. 

## Explanation of the Input/Output Files

### The BEAST File

The BEAST file outlines how to produce the synthetic data. An example is provided in `/examples/testSeqLangGen.xml`; it is reproduced below:

```XML
<beast version='2.0'
       namespace='beast.evolution.alignment:beast.evolution.substitutionmodel'>

    <tree id='tree' spec='beast.util.TreeParser' IsLabelledNewick='true' newick='((((english:0.02096625515232275,(german:0.014857143159686462,french:0.014857143159686462):0.0061091119926362895):0.012862878672687175,spanish:0.033829133825009926):0.029471223948245952,italian:0.06330035777325588):0.0031773962188650223,irish:0.0664777539921209)' />


    <run spec="beast.app.seqgen.LanguageSequenceGen" tree='@tree'>
		<root spec='Sequence' value="01010101010100100010101010000100" taxon="root"/>
		
		<subModel spec='ExplicitBinaryStochasticDollo' birth="0.5" death = "0.5" borrowrate ="0.0" borrowzrate="0.0" noEmptyTrait="false" />	
    <missingModel spec='MissingLanguageModel' rate="0.5"  />
	</run>
</beast> 
```

* The `tree` takes a newick formatted tree with both branch distances and taxon node names. 
  * The tree can also be a randomly generated Yule or Coalsecent tree using standard BEAST format.
* The `run` initiates the plugin using the `tree` defined above. It also has a number of interior parameters:
  * `root` is the sequence to be placed at the root of the tree. It should consist of present (1) or absent traits (0). The plugin does not handle missing or unknown traits. The `taxon` does not need to be *root*.
  * `subModel` defines the model used to simulate evolution down the tree. All models have a `borrowrate` parameter, which defines the rate of global borrowing; `borrowzrate` defines the distance of local borrowing; note: if `borrowzrate` is set to `0.0`, the plugin assumes an infinite distance. Currently there are two models:
    * `ExplicitBinaryGTR` evolves the `root` via a Generalised Time-Reversible model. This model has a single `rate` parameter which defines the rate at which traits both can be birthed, and die. 
    * `ExplicitBinaryStochasticDollo` evolves the `root` via a Stochastic-Dollo model of sequence evolution, which has both a `birth` rate of traits, and a separate `death` rate. 
  * `missingModel` defines the model used to simulate missing data in the final alignment. Currently, this is non-optional and to not use it `rate` should be set to `0`.
    * `MissingLanguageModel` - Each language has a random binomial number of missing events, which convert random cognates in the language to `?`.
    * `MissingMeaningClassModel` - Each meaning class has a random binomial number of missing events, which convert random cognates in the meaning class to `?`.

### The Output file

The Output file is a simple BEAST2 `alignment` piped to `xml`. An example from `/examples/output.xml` can be found below:

```XML
<beast version='2.0'>
<data id='SD' dataType='binary'>
    <sequence taxon='english' value='111111111111111111111111'/>

    <sequence taxon='german' value='111111111111111111111111'/>

    <sequence taxon='french' value='111111111111111111111111'/>

    <sequence taxon='spanish' value='111111111111111111111111'/>

    <sequence taxon='italian' value='111111111111111111011111'/>

    <sequence taxon='irish' value='111111111111111101101111'/>
</data>

<!-- Meaning Classes: 0 -->
<!-- Created at: 2016-04-26 15:09:16.506 -->
</beast>

```

## The Thesis Analysis Package

Included in the repository is a number of classes inside `beastborrowingplugin/thesisanalysis`. These classes are used to do batch analysis of sythetic languages produced by this package under BEAST2 inference. 

These classes are not required for the main running of the program, but may be useful if batch analysis is needed.

The [Morph Models](https://github.com/CompEvol/morph-models) and [Babel](https://github.com/rbouckaert/Babel) packages are required for the running of these classes. 

## About and Contact

This plugin is being written as part of my Computer Science Honours thesis, supervised by [David Welch](https://www.cs.auckland.ac.nz/~davidw/), at the University of Auckland, New Zealand. It is an extension of work undertaken by the Computational Biology Group to create [BEAST2](beast2.org). 

For any questions or queries feel free to contact me at sbra886@aucklanduni.ac.nz. 