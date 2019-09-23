package genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.math3.util.Pair;

import ecology.Species;
import utils.CMUtils;
import utils.ConnSetPair;
import utils.ConnTuple;
import utils.NodeTuple;

/*
 * This class is where the bulk of the computations are done to create a Neural Network object from a genome.
 * When a Transcriptome object is created (which should only be done using a genome object's 'transcribe()' method),
 * it sorts all the genes in the given genome based on their class, and then expresses those genes, eventually creating
 * two Map objects that will persist as long as the Transcriptome does:
 * - 'laysAndNodes', a TreeMap describing the layers that exist in the Network, the nodes that exist in each layer,
 * 	and the biases for each node
 * - 'connWeights', a TreeMap describing all connections in the Network, and their weights.
 * These two will eventually be used by the Organism to create the actual NeuralNetwork.
 */
public class Transcriptome {
	private static int bottomNodes = Species.bottomNodes;
	private static int topNodes = Species.topNodes;
	private static Comparator<Integer> comparator = Species.comparator;
	
		
	private TreeMap<Integer, TreeMap<Integer, Double>> nodeBiasMap = new TreeMap<>(comparator);
	private TreeMap<Integer, TreeMap<Integer, ConnSetPair>> tupleSetMap = new TreeMap<>(comparator);
	private TreeMap<ConnTuple, Double> connWeights = new TreeMap<>();

	/*
	 * Public getter for the 'laysAndNodes' map
	 */
	public TreeMap<Integer, TreeMap<Integer, Double>> getNodeBiasMap() {
		return nodeBiasMap;
	}
	
	/*
	 * Public getter for the 'tupleSetMap'.
	 */
	public TreeMap<Integer, TreeMap<Integer, ConnSetPair>> getTupleSetMap() {
		return tupleSetMap;
	}

	/*
	 * Public getter for the 'connWeights' map
	 */
	public TreeMap<ConnTuple, Double> getConnWeights() {
		return connWeights;
	}
	
	/*
	 * IMPORTANT: This constructor should *only* be called by the Genome.transcribe() method.
	 * This will ensure that all the various Maps and Lists that are created in the process below
	 * are discarded when garbage collection comes around, and only the instance fields stick around.
	 */
	Transcriptome(Genome genome) {
		List<LayerGene> layGenes = new ArrayList<LayerGene>();
		List<NodeGene> nodeGenes = new ArrayList<NodeGene>();
		List<ConnGene> connGenes = new ArrayList<ConnGene>();
//		Gathers all the genes from all the chromosomes in the genome, then sorts them based on Child Class.
		for (Gene gene : poolAllGenes(genome)) {
			if (gene.getClass().equals(LayerGene.class)) layGenes.add((LayerGene) gene);
			else if (gene.getClass().equals(NodeGene.class)) nodeGenes.add((NodeGene) gene);
			else if (gene.getClass().equals(ConnGene.class)) connGenes.add((ConnGene) gene);
		}
		Set<Integer> layerSet = parseLayGenes(layGenes);
		TreeMap<Integer,TreeMap<Integer,PheneDummy>> nodePhenes = fillNodes(nodeGenes, layerSet);
		tupleSetMap = filterNodes(nodePhenes);
		
		
		connWeights = parseConnGenes(connGenes);
		
//		System.out.println("Pre-Removal: ");
//		tupleSetMap.forEach((layNum,nodeMap)->{
//			int sum = 0;
//			for (ConnSetPair pair : nodeMap.values()) {
//				sum += pair.downConns.size();
//			}
//			System.out.print(layNum + "(" + sum + "): [");
//			nodeMap.keySet().forEach(nodeNum -> {
//				System.out.print(nodeNum + ", ");
//			});
//			System.out.println("]");
//		});
//		System.out.println("Total Conns: " + connWeights.size());
//		System.out.println();
		
		removeOrphans(connWeights.navigableKeySet());
		
//		System.out.println("Post-Removal: ");
//		tupleSetMap.forEach((layNum,nodeMap)->{
//			int sum = 0;
//			for (ConnSetPair pair : nodeMap.values()) {
//				sum += pair.downConns.size();
//			}
//			System.out.print(layNum + "(" + sum + "): [");
//			nodeMap.keySet().forEach(nodeNum -> {
//				System.out.print(nodeNum + ", ");
//			});
//			System.out.println("]");
//		});
//		System.out.println("Total Conns: " + connWeights.size());
//		System.out.println();

	}
	
	/*
	 * Called by the Constructor to pool all the non-Centromere genes from a genome into a single, 1-Dimensional List
	 */
	private List<Gene> poolAllGenes(Genome genome) {
		List<Gene> genes = new ArrayList<Gene>();
		Consumer<HomologPair> chromConsumer = (pair) -> genes.addAll(pair.getGenes());
		genome.forEach(chromConsumer);
		return genes;
	}
	
//	LayerGene Stuff
	/*
	 * Goes through all LayerGenes, combines them by layer number, removes those with total xprLevels <= 0,
	 * and returns a Set of Integers representing the remaining layers.
	 */
	private Set<Integer> parseLayGenes(List<LayerGene> layGenes) {
		TreeMap<Integer,List<Double>> layerMap = new TreeMap<Integer,List<Double>>(comparator);
		for (LayerGene gene : layGenes) {
			int layNum = (int) gene.layerNum;
			if (layerMap.containsKey(layNum)) {
				List<Double> list = layerMap.get(layNum);
				list.add(gene.xprLevel);
			}
			else {
				layerMap.put(layNum, new ArrayList<>(Arrays.asList(gene.xprLevel)));
			}
		}
		layerMap.entrySet().removeIf((entry) -> entry.getValue().stream().mapToDouble((d) -> d).sum() < 0.0);
		layerMap.put(-1, null);
		return layerMap.keySet();
	}
	
//	NodeGene Stuff
	private TreeMap<Integer,TreeMap<Integer,PheneDummy>> fillNodes(List<NodeGene> nodeGenes, Set<Integer> layerSet) {
		TreeMap<Integer,TreeMap<Integer,PheneDummy>> nodePhenes = new TreeMap<>();
		for (NodeGene gene: nodeGenes) {
			int layNum = (int) Math.floor(gene.layerNum);
			if (!layerSet.contains(layNum)) continue;
			if (!nodePhenes.containsKey(layNum)) nodePhenes.put(layNum, new TreeMap<Integer,PheneDummy>());
			Map<Integer,PheneDummy> layerMap = nodePhenes.get(layNum);
			int nodeNum = (int) gene.nodeNum;
			if (!layerMap.containsKey(nodeNum)) layerMap.put(nodeNum, new PheneDummy());
			layerMap.get(nodeNum).addGene(gene);
		}
		return nodePhenes;
	}
	
	private TreeMap<Integer,TreeMap<Integer,ConnSetPair>> filterNodes(TreeMap<Integer,TreeMap<Integer,PheneDummy>> nodePhenes) {
		TreeMap<Integer,TreeMap<Integer,ConnSetPair>> tupleSetMap = new TreeMap<Integer,TreeMap<Integer,ConnSetPair>>();
		nodePhenes.forEach((layNum, dummyMap) -> {
			dummyMap.entrySet().removeIf((entry) -> entry.getValue().getXprSum() < 0.0 && layNum != -1);
			TreeMap<Integer,Double> biasMap = new TreeMap<Integer,Double>();
			TreeMap<Integer,ConnSetPair> nodeConnSets = new TreeMap<Integer,ConnSetPair>();
			dummyMap.forEach((nodeNum, phene) -> {
				if (layNum != -1) biasMap.put(nodeNum, phene.getWeightedAvg());
				nodeConnSets.put(nodeNum, new ConnSetPair());
			});
			if (layNum == -1) for (int nodeNum = 0; nodeNum < topNodes; nodeNum++) biasMap.put(nodeNum, 0.0);
			nodeBiasMap.put(layNum, biasMap);
			tupleSetMap.put(layNum,  nodeConnSets);
		});
		return tupleSetMap;
	}
	
//	ConnGene Stuff
	/*
	 * Creates a Map of PheneDummy objects, which represent the phenotype for a given connection, using ConnTuple objects as keys.
	 * Then filters the PheneDummy map based on xprLevel, and uses the remaining connections to populate both the TupleSet objects
	 * in 'tupleMap', and a new Map 'connWeights' with the same ConnTuple keys, but a single Double (representing the connection weight)
	 * as a value.
	 * (PheneDummy is defined below, and more information on its functionality is provided there).
	 */
	private TreeMap<ConnTuple,Double> parseConnGenes(List<ConnGene> connGenes) {
		TreeMap<ConnTuple, PheneDummy> connPhenes = new TreeMap<ConnTuple,PheneDummy>();
		TreeSet<ConnTuple> connTuplesFromGenes = new TreeSet<>();
		for (ConnGene gene : connGenes) {
			ConnTuple connTuple = new ConnTuple(gene);
			connTuplesFromGenes.add(connTuple);
			if (!isConnTupleValid(connTuple)) continue;
			if (!connPhenes.containsKey(connTuple)) connPhenes.put(connTuple, new PheneDummy());
			connPhenes.get(connTuple).addGene(gene);
		}
		TreeMap<ConnTuple,Double> connWeights = new TreeMap<ConnTuple,Double>();
		for (Map.Entry<ConnTuple, PheneDummy> entry : connPhenes.entrySet()) {
			boolean valid = entry.getValue().getXprSum() > 0;
			ConnTuple tuple = entry.getKey();
			if (valid) {
				if (tuple.iLay() != 0) tupleSetMap.get(tuple.iLay()).get(tuple.iNode()).addUp(tuple);
				tupleSetMap.get(tuple.oLay()).get(tuple.oNode()).addDown(tuple);
				connWeights.put(tuple, entry.getValue().getWeightedAvg());
			}
		}
		return connWeights;
	}
	
//	private void loadReasons() {
//		validityMap.put("Top/Bottom in wrong position: ", new TreeSet<>());
//		validityMap.put("Input Layer Matches Output: ", new TreeSet<>());
//		validityMap.put("Bottom NodeNum > " + bottomNodes + ": \t", new TreeSet<>());
//		validityMap.put("Top NodeNum > " + topNodes + ": \t", new TreeSet<>());
//		validityMap.put("Tuple pointing downwards: ", new TreeSet<>());
//		validityMap.put("Non-Existent Input Layer: ", new TreeSet<>());
//		validityMap.put("Non-Existent Input Node: ", new TreeSet<>());
//		validityMap.put("Non-Existent Output Layer: ", new TreeSet<>());
//		validityMap.put("Non-Existent Output Node: ", new TreeSet<>());
//		validityMap.put("Valid: \t\t\t", new TreeSet<>());
//	}
	
	/*
	 * checks to make sure a given tuple is valid (IE, the connection doesn't point backwards, and both input and output nodes exist)
	 */
	private boolean isConnTupleValid(ConnTuple tuple) {
		if (tuple.iLay() == -1 || tuple.oLay() == 0) return false;
		if (tuple.iLay() == tuple.oLay()) return false;
		boolean iValid = false;
		boolean oValid = false;
		if (tuple.iLay() == 0) {
			if (tuple.iNode() < bottomNodes) iValid = true;
			else return false;
		}
		if (tuple.oLay() == -1) {
			if (tuple.oNode() < topNodes) oValid = true;
			else return false;
		}
		if (!(iValid && oValid)) {
			if (!(comparator.compare(tuple.iLay(), tuple.oLay()) < 0)) return false;
			if (!iValid) {
				if (!nodeBiasMap.containsKey(tuple.iLay())) return false;
				else if (!nodeBiasMap.get(tuple.iLay()).containsKey(tuple.iNode())) return false;
				else iValid = true;
			}
			if (!oValid) {
				if (!nodeBiasMap.containsKey(tuple.oLay())) return false;
				else if (!nodeBiasMap.get(tuple.oLay()).containsKey(tuple.oNode())) return false;
				else oValid = true;
			}
		}
		return iValid && oValid;
	}

//	Orphan Stuff
	/*
	 * Used to remove orphans. An orphan node is a node that either has no connections leading into it,
	 * or leading out of it. An orphan layer is simply a layer with no non-orphan nodes.
	 * Removal of orphans is done at this step to speed up runtime when the Network is eventually built.
	 */
	private void removeOrphans(NavigableSet<ConnTuple> connTuples) {
		tupleSetMap.entrySet().removeIf(entry -> {
			int layNum = entry.getKey();
			if (layNum == -1) return false;
			return isLayerOrphan(connTuples, tupleSetMap.get(layNum), true);
		});
		tupleSetMap.descendingMap().entrySet().removeIf(entry -> {
			int layNum = entry.getKey();
			if (layNum == -1) return false;
			return isLayerOrphan(connTuples, tupleSetMap.get(layNum), false);
		});
		for (ConnSetPair setPair : tupleSetMap.get(-1).values()) setPair.downConns.retainAll(connTuples);
		
		nodeBiasMap.keySet().retainAll(tupleSetMap.keySet());
		nodeBiasMap.forEach((layNum, nodeMap) -> {
			nodeMap.keySet().retainAll(tupleSetMap.get(layNum).keySet());
		});
	}
	
	private boolean isLayerOrphan(NavigableSet<ConnTuple> connTuples, TreeMap<Integer,ConnSetPair> layConns, boolean direction) {
		layConns.entrySet().removeIf((entry) -> {
			ConnSetPair connSetPair = entry.getValue();
			Set<ConnTuple> connSet = (direction)? connSetPair.downConns : connSetPair.upConns;
			connSet.retainAll(connTuples);
			boolean orphanNode = connSet.isEmpty();
			if (orphanNode){
				Set<ConnTuple> otherSet = (direction)? connSetPair.upConns : connSetPair.downConns;
				connTuples.removeAll(otherSet);
			}
			return orphanNode;
		});
		return layConns.isEmpty();
	}
	
//	HELPER CLASSES
	
	/*
	 * Used briefly in parsing NodeGenes and ConnGenes.
	 * Represents a phenotype for a Node/Connection, that is essentially a conglomerate of the expression of all
	 * genes that correspond for that Node/Connection. It is used to add up the xprLevels for filtering, and
	 * for calculating a single bias/weight value from all the genes, once filtered.
	 */
	private class PheneDummy {
		List<Double> xprVals = new ArrayList<Double>();
		List<Pair<Double, Double>> valPairs = new ArrayList<Pair<Double,Double>>();
		
		void addGene(NodeGene gene) {
			xprVals.add(gene.xprLevel);
			valPairs.add(new Pair<Double,Double>(gene.xprLevel, gene.bias));
		}
		
		void addGene(ConnGene gene) {
			xprVals.add(gene.xprLevel);
			valPairs.add(new Pair<Double,Double>(gene.xprLevel, gene.weight));
		}
		
		double getXprSum() {
			return xprVals.stream().mapToDouble((d) -> d).sum();
		}
		
		double getWeightedAvg() {
			return valPairs.stream().mapToDouble((pair) -> pair.getFirst()*pair.getSecond()).sum()/getXprSum();
		}
	}

}
