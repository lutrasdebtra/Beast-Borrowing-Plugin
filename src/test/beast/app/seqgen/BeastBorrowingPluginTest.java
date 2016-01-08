package test.beast.app.seqgen;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import beast.app.seqgen.LanguageSequenceGen;
import beast.evolution.alignment.Sequence;
import beast.evolution.substitutionmodel.ExplicitBinaryGTR;
import beast.evolution.substitutionmodel.ExplicitBinaryStochasticDollo;
import beast.evolution.substitutionmodel.LanguageSubsitutionModel;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.util.Randomizer;

public class BeastBorrowingPluginTest {
	public static void main(String[] args) throws Exception {
		run();
	}

	private static void run() throws Exception {
		// Base Seq generation.
		String seq = "";
		for (int i = 0; i < 5; i++) {
			seq += '1';
		}
		/*
		Sequence test = new Sequence("", "01010");
		System.out.println(test.getData());
		String newSeq = LanguageSubsitutionModel.replaceCharAt(test.getData(), 0, "1");
		System.out.println(newSeq);
		test.dataInput.setValue(newSeq, test);
		System.out.println(test.getData());
		*/		

		//GTRTest(seq);
		//SDTest(seq);
		//TreeGenTest(seq);
		//TreeSDBorrowingTest(seq);
		//TreeGTRBorrowingTest(seq);
		
		//SDTreeValidation();
		//GTRTreeValidation();
		//GTRTreeBorrowingValidationTwoLanguages();
		//GTRTreeBorrowingValidationThreeLanguages();
		//SDTreeBorrowingValidation();
		//SeqGenTest();
		NoEmptyTraitTest();
		//SpeedTestNonBorrowing();
	}

	private static void GTRTest(String seq) throws Exception {
		Sequence l = new Sequence("",seq);

		System.out.println("GTR Test");
		System.out.println(l.getData());

		ExplicitBinaryGTR gtr_mod = new ExplicitBinaryGTR(0.5,0.0,0.0, false);
		Sequence gtrLang = gtr_mod.mutateLang(l, 10);
		System.out.println(gtrLang.getData());
	}

	private static void SDTest(String seq) throws Exception {

		Sequence l2 = new Sequence("",seq);
	
		System.out.println("SD Test");
		System.out.println(l2.getData());
		ExplicitBinaryStochasticDollo sd_mod = new ExplicitBinaryStochasticDollo(0.5, 0.5,0.0,0.0, false);
		Sequence sdLang = sd_mod.mutateLang(l2, 10);
		System.out.println(sdLang.getData());
	}

	private static void TreeGenTest(String seq) throws Exception {
		Sequence l = new Sequence("",seq);
		ExplicitBinaryStochasticDollo sd_mod = new ExplicitBinaryStochasticDollo(0.5, 0.5,0.0,0.0, false);

		System.out.println("Tree generation test");
		Node rootNode = new Node();
		rootNode.setMetaData("lang", l);
		rootNode.setHeight(0);
		Tree tree = new Tree(rootNode);
		tree = randomTree(tree, 4, 0.6);
		tree.getRoot().setMetaData("lang", l);
		sd_mod.mutateOverTree(tree);
		for (Node n : tree.getExternalNodes()) {
			Sequence l2 = (Sequence) n.getMetaData("lang");
			System.out.println(l2.getData());
		}
	}

	private static void TreeSDBorrowingTest(String seq) throws Exception {
		seq = "";
		double pos = Randomizer.nextPoisson(10.0);
		for (int j = 0; j < pos; j++) {
			seq += '1';
		}
		Sequence l = new Sequence("",seq);

		ExplicitBinaryStochasticDollo sd_mod = new ExplicitBinaryStochasticDollo(0.0022314355, 0.00022314355, 0.1, 0.0, false);

		System.out.println("Tree SD Borrowing Test");
		Node rootNode = new Node();
		rootNode.setMetaData("lang", l);
		rootNode.setHeight(0);
		Tree tree = new Tree(rootNode);
		tree = randomTree(tree, 4, 0.01);
		sd_mod.mutateOverTreeBorrowing(tree);
		for (Node n : tree.getExternalNodes()) {
			Sequence l2 = (Sequence) n.getMetaData("lang");
			System.out.println(l2.getData());
		}
	}
	
	private static void TreeGTRBorrowingTest(String seq) throws Exception {
		Sequence l = new Sequence("",seq);
		ExplicitBinaryGTR gtr_mod = new ExplicitBinaryGTR(0.5, 1.2,0.0, false);

		System.out.println("Tree GTR Borrowing Test");
		Node rootNode = new Node();
		rootNode.setMetaData("lang", l);
		rootNode.setHeight(0);
		Tree tree = new Tree(rootNode);
		tree = randomTree(tree, 8, 0.6);
		gtr_mod.mutateOverTreeBorrowing(tree);
		for (Node n : tree.getExternalNodes()) {
			Sequence l2 = (Sequence) n.getMetaData("lang");
			System.out.println(l2.getData());
		}
	}
	
	private static void GTRValidation() throws Exception {
		ArrayList<Integer> births = new ArrayList<Integer>();
		for (int i = 0; i < 100000; i++) {
			System.out.println(i);
			String seq = "";
			for (int j = 0; j < 20; j++) {
				seq += Integer.toString(Randomizer.nextInt(2));
			}
			Sequence l = new Sequence("",seq);
			ExplicitBinaryGTR gtr_mod = new ExplicitBinaryGTR(0.5,0.0,0.0, false);
			Sequence gtrLang = gtr_mod.mutateLang(l, 100);
			births.add(LanguageSubsitutionModel.getBirths(gtrLang));
		}
		//listToCSV(births, "C:/Users/Stuart/Google Drive/University/Year 5 - Honours/Thesis/R_Code/gtr.csv");
		listToCSV(births, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr.csv");
	}
	
	private static void SDValidation() throws Exception {
		ArrayList<Integer> births = new ArrayList<Integer>();
		for (int i = 0; i < 100000; i++) {
			System.out.println(i);
			ExplicitBinaryStochasticDollo sd_mod = new ExplicitBinaryStochasticDollo(0.5, 0.5,0.0,0.0, false);
			String seq = "";
			Sequence l = new Sequence("",seq);
			Sequence sdLang = sd_mod.mutateLang(l, 100);
			births.add(LanguageSubsitutionModel.getBirths(sdLang));
		}
		listToCSV(births, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/sd.csv");
		//listToCSV(births, "/home/stuart/Code/Beast2-plugin/Beast-Borrowing-Plugin/Utilities/Thesis Graph Generation/sd.csv");

	}
	
	private static void GTRTreeValidation() throws Exception {
		ArrayList<Integer> births = new ArrayList<Integer>();
		for (int i = 0; i < 100000; i++) {
			System.out.println(i);
			ExplicitBinaryGTR gtr_mod = new ExplicitBinaryGTR(0.5,0.0,0.0, false);
			String seq = "";
			for (int j = 0; j < 20; j++) {
				seq += Integer.toString(Randomizer.nextInt(2));
			}
			Sequence l = new Sequence("",seq);
			Node rootNode = new Node();
			rootNode.setMetaData("lang", l);
			rootNode.setHeight(0);
			Tree tree = new Tree(rootNode);
			tree = randomTree(tree, 8, 0.6);
			tree = gtr_mod.mutateOverTree(tree);
			for (Node n : tree.getExternalNodes()) {
				Sequence l2 = (Sequence) n.getMetaData("lang");
				births.add(LanguageSubsitutionModel.getBirths(l2));
			}
			
		}
		listToCSV(births, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtrtree.csv");
		//listToCSV(births, "/home/stuart/Code/Beast2-plugin/Beast-Borrowing-Plugin/Utilities/Thesis Graph Generation/sdtree.csv");
	}
	
	private static void SDTreeValidation() throws Exception {
		ArrayList<Integer> births = new ArrayList<Integer>();
		
		for (int i = 0; i < 100000; i++) {
			System.out.println(i);
			ExplicitBinaryStochasticDollo sd_mod = new ExplicitBinaryStochasticDollo(0.5, 0.5,0.0,0.0, false);
			String seq = "";
			Sequence l = new Sequence("",seq);
			Node rootNode = new Node();
			rootNode.setMetaData("lang", l);
			rootNode.setHeight(0);
			Tree tree = new Tree(rootNode);
			tree = randomTree(tree, 8, 0.01);
			tree = sd_mod.mutateOverTree(tree);			
			for (Node n : tree.getExternalNodes()) {
				Sequence l2 = (Sequence) n.getMetaData("lang");
				births.add(LanguageSubsitutionModel.getBirths(l2));			
			}
		}
		listToCSV(births, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/sdtree.csv");
		//listToCSV(births, "/home/stuart/Code/Beast2-plugin/Beast-Borrowing-Plugin/Utilities/Thesis Graph Generation/sdtree.csv");
	}
	
	private static void GTRTreeBorrowingValidationTwoLanguages() throws Exception {
		Tree tree = null;
		ArrayList<Integer> zeroZero = new ArrayList<Integer>();
		ArrayList<Integer> zeroOne = new ArrayList<Integer>();
		ArrayList<Integer> oneZero = new ArrayList<Integer>();
		ArrayList<Integer> oneOne = new ArrayList<Integer>();
		for (int i = 0; i < 100000; i++) {
			System.out.println(i);
			ExplicitBinaryGTR gtr_mod = new ExplicitBinaryGTR(0.5,0.5,0.0, false);
			String seq = "";
			for (int j = 0; j < 100; j++) {
				seq += Integer.toString(Randomizer.nextInt(2));
			}
			Sequence l = new Sequence("",seq);
			Node rootNode = new Node();
			rootNode.setMetaData("lang", l);
			rootNode.setHeight(0);
			tree = new Tree(rootNode);
			tree = randomTree(tree, 2, 0.1);
			tree = gtr_mod.mutateOverTreeBorrowing(tree);
			List<Node> ext = tree.getExternalNodes();
			String l1 = ((Sequence) ext.get(0).getMetaData("lang")).getData();
			String l2 = ((Sequence) ext.get(1).getMetaData("lang")).getData();
			int zeroZeroInt = 0;
			int zeroOneInt = 0;
			int oneZeroInt = 0;
			int oneOneInt = 0;
			for (int j = 0; j < 20; j++) {
				if (l1.charAt(j) == '0' && l2.charAt(j) == '0') {
					zeroZeroInt += 1;
				} else if (l1.charAt(j) == '0' && l2.charAt(j) == '1') {
					zeroOneInt += 1;
				} else if (l1.charAt(j) == '1' && l2.charAt(j) == '0') {
					oneZeroInt += 1;
				} else {
					oneOneInt += 1;
				}
			}
			zeroZero.add(zeroZeroInt);
			zeroOne.add(zeroOneInt);
			oneZero.add(oneZeroInt);
			oneOne.add(oneOneInt);
		}
		listToCSV(zeroZero, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr00.csv");
		listToCSV(zeroOne, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr01.csv");
		listToCSV(oneZero, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr10.csv");
		listToCSV(oneOne, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr11.csv");
	}
	
	private static void GTRTreeBorrowingValidationThreeLanguages() throws Exception {
		Tree tree = null;
		ArrayList<Integer> zeroZeroZero = new ArrayList<Integer>();
		ArrayList<Integer> oneZeroZero = new ArrayList<Integer>();
		ArrayList<Integer> zeroOneZero = new ArrayList<Integer>();
		ArrayList<Integer> zeroZeroOne = new ArrayList<Integer>();
		ArrayList<Integer> oneOneZero = new ArrayList<Integer>();
		ArrayList<Integer> oneZeroOne = new ArrayList<Integer>();
		ArrayList<Integer> zeroOneOne = new ArrayList<Integer>();
		ArrayList<Integer> oneOneOne = new ArrayList<Integer>();
		for (int i = 0; i < 100000; i++) {
			System.out.println(i);
			ExplicitBinaryGTR gtr_mod = new ExplicitBinaryGTR(0.5,0.5,0.0, false);
			String seq = "";
			for (int j = 0; j < 10000; j++) {
				seq += Integer.toString(Randomizer.nextInt(2));
			}
			Sequence l = new Sequence("",seq);
			Node rootNode = new Node();
			rootNode.setMetaData("lang", l);
			rootNode.setHeight(0);
			tree = new Tree(rootNode);
			tree = randomTree(tree, 3, 0.1);
			tree = gtr_mod.mutateOverTreeBorrowing(tree);
			List<Node> ext = tree.getExternalNodes();
			String l1 = ((Sequence) ext.get(0).getMetaData("lang")).getData();
			String l2 = ((Sequence) ext.get(1).getMetaData("lang")).getData();
			String l3 = ((Sequence) ext.get(2).getMetaData("lang")).getData();
			Integer zeroZeroZeroInt = 0;
			Integer oneZeroZeroInt = 0;
			Integer zeroOneZeroInt = 0;
			Integer zeroZeroOneInt = 0;
			Integer oneOneZeroInt = 0;
			Integer oneZeroOneInt = 0;
			Integer zeroOneOneInt = 0;
			Integer oneOneOneInt = 0;
			for (int j = 0; j < 20; j++) {
				if (l1.charAt(j) == '0' && l2.charAt(j) == '0' && l3.charAt(j) == '0') {
					zeroZeroZeroInt += 1;
				} else if (l1.charAt(j) == '1' && l2.charAt(j) == '0' && l3.charAt(j) == '0') {
					oneZeroZeroInt += 1;
				} else if (l1.charAt(j) == '0' && l2.charAt(j) == '1' && l3.charAt(j) == '0') {
					zeroOneZeroInt += 1;
				} else if (l1.charAt(j) == '0' && l2.charAt(j) == '0' && l3.charAt(j) == '1') {
					zeroZeroOneInt += 1;
				} else if (l1.charAt(j) == '1' && l2.charAt(j) == '1' && l3.charAt(j) == '0') {
					oneOneZeroInt += 1;
				} else if (l1.charAt(j) == '1' && l2.charAt(j) == '0' && l3.charAt(j) == '1') {
					oneZeroOneInt += 1;
				} else if (l1.charAt(j) == '0' && l2.charAt(j) == '1' && l3.charAt(j) == '1') {
					zeroOneOneInt += 1;
				} else if (l1.charAt(j) == '1' && l2.charAt(j) == '1' && l3.charAt(j) == '1') {
					oneOneOneInt += 1;
				}
			}
			zeroZeroZero.add(zeroZeroZeroInt);
			oneZeroZero.add(oneZeroZeroInt);
			zeroOneZero.add(zeroOneZeroInt);
			zeroZeroOne.add(zeroZeroOneInt);
			oneOneZero.add(oneOneZeroInt);
			oneZeroOne.add(oneZeroOneInt);
			zeroOneOne.add(zeroOneOneInt);
			oneOneOne.add(oneOneOneInt);
		}
		listToCSV(zeroZeroZero, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr000.csv");
		listToCSV(oneZeroZero, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr100.csv");
		listToCSV(zeroOneZero, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr010.csv");
		listToCSV(zeroZeroOne, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr001.csv");
		listToCSV(oneOneZero, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr110.csv");
		listToCSV(oneZeroOne, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr101.csv");
		listToCSV(zeroOneOne, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr011.csv");
		listToCSV(oneOneOne, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/gtr111.csv");
	}
	
	private static void SDTreeBorrowingValidation() throws Exception {
		Tree tree = null;
		ArrayList<Integer> births = new ArrayList<Integer>();
		for (int i = 0; i < 1000; i++) {
			System.out.println(i);
			ExplicitBinaryStochasticDollo sd_mod = new ExplicitBinaryStochasticDollo(0.0022314355, 0.00022314355,0.1,0.0, false);
			String seq = "";
			Integer pos =  (int) Randomizer.nextPoisson(10.0);
			for (int j = 0; j < pos; j++) {
				seq += '1';
			}
			Sequence l = new Sequence("",seq);
			Node rootNode = new Node();
			rootNode.setMetaData("lang", l);
			rootNode.setHeight(0);
			tree = new Tree(rootNode);
			tree = randomTree(tree, 8, 0.01);
			tree = sd_mod.mutateOverTreeBorrowing(tree);
			for (Node n : tree.getExternalNodes()) {
				Sequence l2 = (Sequence) n.getMetaData("lang");
				births.add(LanguageSubsitutionModel.getBirths(l2));
			}
		}
		listToCSV(births, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/sdtreeborrowing.csv");
	}
	
	private static void SpeedTestNonBorrowing() throws Exception {
		ArrayList<Long> GTRA1 = new ArrayList<Long>();
		ArrayList<Long> GTRA2 = new ArrayList<Long>();
		ArrayList<Long> SDA1 = new ArrayList<Long>();
		ArrayList<Long> SDA2 = new ArrayList<Long>();
		ExplicitBinaryStochasticDollo sd_mod = new ExplicitBinaryStochasticDollo(0.5, 0.5,0.0,0.0, false);
		ExplicitBinaryGTR gtr_mod = new ExplicitBinaryGTR(0.5,0.0,0.0, false);
		Tree tree = null;
		long startTime, endTime;
		for (int i = 0; i < 10000; i++) {
			System.out.println(i);
			String seq = "";
			Integer pos =  (int) Randomizer.nextPoisson(10.0);
			for (int j = 0; j < pos; j++) {
				seq += '1';
			}
			Sequence l = new Sequence("",seq);
			Node rootNode = new Node();
			rootNode.setMetaData("lang", l);
			rootNode.setHeight(0);
			tree = new Tree(rootNode);
			tree = randomTree(tree, 8, 0.06);
			
			startTime = System.nanoTime();
			tree = gtr_mod.mutateOverTree(tree);
			endTime = System.nanoTime();
			GTRA1.add((endTime-startTime)/1000000);
			
			startTime = System.nanoTime();
			tree = gtr_mod.mutateOverTreeBorrowing(tree);	
			endTime = System.nanoTime();
			GTRA2.add((endTime-startTime)/1000000);
			
			startTime = System.nanoTime();
			tree = sd_mod.mutateOverTree(tree);
			endTime = System.nanoTime();
			SDA1.add((endTime-startTime)/1000000);
			
			startTime = System.nanoTime();
			tree = sd_mod.mutateOverTreeBorrowing(tree);	
			endTime = System.nanoTime();
			SDA2.add((endTime-startTime)/1000000);
			
			listToCSV(GTRA1, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/speed_gtr_a1.csv");
			listToCSV(GTRA2, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/speed_gtr_a2.csv");
			listToCSV(SDA1, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/speed_sd_a1.csv");
			listToCSV(SDA2, "C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/Utilities/Thesis Graph Generation/speed_sd_a2.csv");
		}
	}
	
	private static void SeqGenTest() {
		String[] args = {"C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/examples/testSeqLangGen.xml","2","C:/Users/Stuart/workspace/Beast2BorrowingSequenceSimulator/examples/output.xml"};
		//String[] args = {"/home/stuart/Code/Beast2-plugin/Beast-Borrowing-Plugin/examples/testSeqLangGen.xml","2","/home/stuart/Code/Beast2-plugin/Beast-Borrowing-Plugin/examples/output.xml"};
		LanguageSequenceGen.main(args);
	}
	
	private static void NoEmptyTraitTest() throws Exception {
		for (int i = 0; i < 1; i++) {
			System.out.println(i);
			Sequence l = new Sequence("","00000000000001");
			Node rootNode = new Node();
			rootNode.setMetaData("lang", l);
			rootNode.setHeight(0);
			Tree tree = new Tree(rootNode);
			tree = randomTree(tree, 3, 0.06);
			ExplicitBinaryStochasticDollo sd_mod = new ExplicitBinaryStochasticDollo(0.0, 0.5,0.0,0.0, true);
			tree = sd_mod.mutateOverTree(tree);
			
			for (Node n : tree.getExternalNodes()) {
				Sequence l2 = (Sequence) n.getMetaData("lang");
				System.out.println(l2.getData());
			}
			
		}
	}

	private static void printTree(Tree base) {
		System.out.println("Printing Tree");

		for (Node node : base.listNodesPostOrder(base.getRoot(), null)) {
			System.out.println(((Sequence) node.getMetaData("lang")).getData());
		}
	}

	private static <T> void listToCSV(ArrayList<T> l, String fileName) {
		final String NEW_LINE_SEPARATOR = "\n";
		FileWriter fW = null;

		try {
			fW = new FileWriter(fileName);
			for ( T i : l) {
				fW.append(String.valueOf(i));
				fW.append(NEW_LINE_SEPARATOR);
			}

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fW.flush();
				fW.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}
	}
	
	private static Tree randomTree(Tree rootTree, Integer numLeaves, Double branchRate) {
		ArrayList<Node> currLeaves = new ArrayList<Node>(); 
		ArrayList<Node> newLeaves = new ArrayList<Node>();
		currLeaves.add(rootTree.getRoot());
		Sequence rootLang = (Sequence) rootTree.getRoot().getMetaData("lang");
		Node childLeft, childRight;
		
		while (currLeaves.size() < numLeaves) {
			for (Node parent : currLeaves) {
				childLeft = new Node();
				childRight = new Node();
				
				// Left child.
				double t = Randomizer.nextExponential(branchRate);
				childLeft.setParent(parent);
				parent.addChild(childLeft);
				childLeft.setHeight(parent.getHeight()+t);
				childLeft.setMetaData("lang", rootLang);
				newLeaves.add(childLeft);
				rootTree.addNode(childLeft);
				// Right child.
				//t = Randomizer.nextExponential(branchRate);
				childRight.setParent(parent);
				parent.addChild(childRight);
				childRight.setHeight(parent.getHeight()+t);
				childRight.setMetaData("lang", rootLang);
				newLeaves.add(childRight);
				rootTree.addNode(childRight);
			}
			currLeaves = new ArrayList<Node>(newLeaves);
			newLeaves = new ArrayList<Node>();
		}
		return rootTree;
	}
	
}
