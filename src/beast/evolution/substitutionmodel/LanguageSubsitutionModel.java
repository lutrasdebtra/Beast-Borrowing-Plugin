package beast.evolution.substitutionmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.sun.xml.internal.ws.util.StringUtils;

import beast.core.CalculationNode;
import beast.core.Description;
import beast.core.Input;
import beast.evolution.alignment.Sequence;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;


/*
 * LanguageSubsitutionModel Abstract Class
 * 
 * This class is a basis for other Language Substitution models (LSMs), it contains both abstract methods 
 * required for any LSM, as well as various helper methods.
 * 
 * @author Stuart Bradley (sbra886@aucklanduni.ac.nz)
 * @version 1.0
 * 
 */
@Description("Abstract class for language mutation models")
public abstract class LanguageSubsitutionModel extends CalculationNode {
	public Input<Double> borrowInput = new Input<Double>("borrowrate", "borrowing rate");
	public Input<Double> borrowZInput = new Input<Double>("borrowzrate", "local borrowing distance");
	public Input<Boolean> noEmptyTraitInput = new Input<Boolean>("noEmptyTrait", "no empty trait");
	
	protected double borrowRate;
	protected double borrowZ;
	protected boolean noEmptyTrait;
	
	/*
	 * ABSTRACT METHODS
	 */
	
	
	/*
	 * BEAST Object required class.
	 * @see beast.evolution.substitutionmodel.SubstitutionModel.Base#initAndValidate()
	 */
	public abstract void initAndValidate();
	
	/*
	 * Single lineage language mutation.
	 * @param l Language, initial language.
	 * @param T double, mutation time.
	 * @return Language, mutated language.
	 */
	public abstract Sequence mutateLang(Sequence l, double T) throws Exception;
	
	/*
	 * Basic full tree mutation, each branch uses mutateLang(l,c,T).
	 * @param base Tree, initial tree with a root language. (Metadata("lang")).
	 * @return Tree, final tree with a full set of mutated languages. 
	 */
	public abstract Tree mutateOverTree(Tree base) throws Exception;
	
	/*
	 * Full tree mutation with borrowing.
	 * @param base Tree, initial tree with a root language. (Metadata("lang")).
	 * @param borrow borrowing rate.
	 * @param z local borrowing rate, 0.0 rate implies global borrowing. 
	 * @return base Tree with languages added. 
	 */
	public abstract Tree mutateOverTreeBorrowing(Tree base) throws Exception;
	
	/*
	 * Probabilities for different events.
	 * @param aliveNodes, see aliveNodes(base, t).
	 * @param borrow borrowing rate.
	 * @return double[], array of probabilities.
	 */
	protected abstract double[] BorrowingProbs(ArrayList<Node> aliveNodes) throws Exception;
	
	/*
	 * Total rate of mutation.
	 * @param aliveNodes, see aliveNodes(base, t).
	 * @param borrow borrowing rate.
	 * @return Double, total rate,
	 */
	protected abstract Double totalRate (ArrayList<Node> aliveNodes) throws Exception;
	
	public abstract void setBirthRate(Double r);
	
	public abstract Double getBirthRate();
	
	/*
	 * NORMAL METHODS
	 */
	
	
	/*
	 * Language index in random order.
	 * @param l Language.
	 * @return shuffled indexes of each letter in l.
	 */
	protected ArrayList<Integer> getRandLangIndex(Sequence l) {
		ArrayList<Integer> randInts = new ArrayList<Integer>();
		for (int i = 0; i < l.getData().length(); i++) {
			randInts.add(i);
		}
		
		Collections.shuffle(randInts);
		
		return randInts;
		
	}
	
	/*
	 * Local borrowing distance between two languages (nodes)
	 * @param L1, L2, Nodes; two languages to be compared.
	 * @param z Double, distance.
	 * @return Boolean as to whether they are within z. 
	 */
	protected boolean localDist(Node L1, Node L2) {
		// If z is 0, global borrowing is in effect.
		if (borrowZ == 0.0) {
			return true;
		}
		Node parent1, parent2;
		Double dist1 = 0.0, dist2 = 0.0;
		while (dist1 <= borrowZ && dist2 <= borrowZ) {
			parent1 = L1.getParent();
			parent2 = L1.getParent();
			
			// If it's the same ancestor, return true.
			if (parent1 == parent2) {
				return true;
			}
			
			// Reduce height: leaves -> root.
			dist1 = L1.getHeight() - parent1.getHeight();
			dist2 = L2.getHeight() - parent2.getHeight();
			
			L1 = parent1;
			L2 = parent2;
		}
		return false;
	}
	
	/*
	 * Gets height of tree.
	 * @param base Tree.
	 * @return height of base. 
	 */
	protected Double getTreeHeight(Tree base) {
		Node[] nodes = base.getNodesAsArray();
		// Comparator that sorts nodes on height.
	    Arrays.sort(nodes, new Comparator<Node>() {
	        @Override
	        public int compare(Node o1, Node o2) {
	            return new Double (o1.getHeight()).compareTo(o2.getHeight());
	        }
	    });	
	    // Gets largest node.
	    return nodes[nodes.length-1].getHeight();
	}
	
	/*
	 * Sets the language of a node and all its decendents.
	 * @param subRoot Node, root node.
	 * @param newLang Language, new language to set.
	 */
	protected void setSubTreeLanguages(Node subRoot, Sequence newLang) {
		subRoot.setMetaData("lang", newLang);
		for (Node n : subRoot.getAllChildNodes()) {
			n.setMetaData("lang", newLang);
		}
	}
	
	/*
	 * Gets nodes alive at time t.
	 * @param base Tree.
	 * @param t Double, alive time.
	 * @return list of alive nodes.
	 */
	protected ArrayList<Node> getAliveNodes(Tree base, Double t) {
		ArrayList<Node> aliveNodes = new ArrayList<Node>();
		
		Node root = base.getRoot();
		for (Node child : root.getChildren()) {
			if (child.getHeight() >= t) {
				aliveNodes.add(child);
			} else {
				aliveNodes.addAll(aliveNodes(child, t));
			}
		}

		return aliveNodes;
	}
	
	/*
	 * Recursive inner method.
	 * @see beast.evolution.substitutionmodel.SubstitutionModel.Base.LanguageSubsitutionModel#getAliveNodes(beast.evolution.tree.Tree, double)
	 */
	protected ArrayList<Node> aliveNodes(Node curr, Double t) {
		ArrayList<Node> aN = new ArrayList<Node>();
		for (Node child : curr.getChildren()) {
			if (child.getHeight() >= t) {
				aN.add(child);
			} else {
				aN.addAll(aliveNodes(child, t));
			}
		}
		return aN;
	}
	
	/*
	 * No Empty Trait Check.
	 * @param l Sequence.
	 * @return boolean whether or not trait can be removed.
	 */
	public boolean noEmptyTraitCheck(Sequence l) {
		// Traits are removal if noEmptyTrait flag is not set.
		if (noEmptyTrait == false) {
			return true;
		} else {
			// Check if removing a trait kills meaning class.
			if (getBirths(l) > 1) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/*
	 * Gets Sequence from Node.
	 * @param n Node.
	 * @return Sequence.
	 */
	public static Sequence getSequence(Node n) throws Exception {
		try {
			return (Sequence) n.getMetaData("lang");
		} catch (ClassCastException e) {
			return new Sequence(n.metaDataString, "");
		}
	}
	
	/*
	 * Gets number of alive traits (1's).
	 * @param l Sequence.
	 * @return count of 1's.
	 */
	public static int getBirths(Sequence l) {
		String seq = l.getData();
		int count = 0;
		for (char c : seq.toCharArray()) {
			if (Character.getNumericValue(c) == 1) {
				count += 1;
			}
		}
		return count;
	}
	
	/*
	 * Replaces string (c) in string (s).
	 * @param s String.
	 * @param pos Int position.
	 * @param c replacement String.
	 * @return modified String.
	 */
	public static String replaceCharAt(String s, int pos, String c) {
		return s.substring(0,pos) + c + s.substring(pos+1);
	}
	
	/*
	 * Auto-generated getters/setters.
	 */

	public double getBorrowRate() {
		return borrowRate;
	}

	public void setBorrowRate(double b) {
		this.borrowRate = b;
	}

	public double getBorrowZ() {
		return borrowZ;
	}

	public void setBorrowZ(double z) {
		this.borrowZ = z;
	}
	
	public boolean getNoEmptyTrait() {
		return noEmptyTrait;
	}
	
	public void setNoEmptyTrait(boolean noEmptyTrait) {
		this.noEmptyTrait = noEmptyTrait;
	}
}
