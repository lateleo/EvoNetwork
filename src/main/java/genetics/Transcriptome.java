package genetics;

import java.util.ArrayList;
import java.util.Arrays;
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
import utils.ConnTuple;

/*
 * This class is where the bulk of the computations are done to create a Neural Network object from a genome.
 * When a Transcriptome object is created (which should only be done using a genome object's 'transcribe()' method),
 * it sorts all the genes in the given genome based on their class, and then expresses those genes, eventually creating
 * two Map objects that will persist as long as the Transcriptome does:
 * - 'laysAndNodes', a TreeMap describing the layers that exist in the Network, the nodes that exist in each layer,
 * 	and the biases for each node
 * - 'connWeights', a Hashtable describing all connections in the Network, and their weights.
 * These two will eventually be used by the Organism to create the actual NeuralNetwork.
 */
public class Transcriptome {
	private static int bottomNodes = Species.bottomNodes;
	private static int topNodes = Species.topNodes;
	
	private Map<Integer,Map<Integer,Double>> laysAndNodes = new TreeMap<>();
	private Map<ConnTuple,Double> connWeights = new Hashtable<>();
	
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
		TreeMap<Integer,TreeMap<Integer,TupleSet>> tupleMap = parseNodeGenes(nodeGenes, layerSet);
		TreeMap<ConnTuple,Double> connWeights = parseConnGenes(connGenes, tupleMap);
		removeOrphans(connWeights.navigableKeySet(), tupleMap);
	}
	
	/*
	 * Public getter for the 'laysAndNodes' map
	 */
	public Map<Integer, Map<Integer, Double>> getLaysAndNodes() {
		return laysAndNodes;
	}

	/*
	 * Public getter for the 'connWeights' map
	 */
	public Map<ConnTuple, Double> getConnWeights() {
		return connWeights;
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
		TreeMap<Integer,List<Double>> layerMap = new TreeMap<Integer,List<Double>>();
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
		layerMap.entrySet().removeIf((entry) -> entry.getValue().stream().mapToDouble((d) -> d).sum() < 0);
		layerMap.put(-1, null);
		return layerMap.keySet();
	}
	
//	NodeGene Stuff
	/*
	 * Creates a multi-layer Map to organize node phenotypes, filters them based on xprLevel, then creates a new map that
	 * also contains TupleSet objects, which will eventually contain information regarding the connections leading into
	 * and out of the remaining nodes.
	 */
	private TreeMap<Integer,TreeMap<Integer,TupleSet>> parseNodeGenes(List<NodeGene> nodeGenes, Set<Integer> layerSet) {
		Map<Integer,Map<Integer,PheneDummy>> nodePhenes = fillNodes(nodeGenes, layerSet);
		return filterNodes(nodePhenes);
	}

	private Map<Integer,Map<Integer,PheneDummy>> fillNodes(List<NodeGene> nodeGenes, Set<Integer> layerSet) {
		Map<Integer,Map<Integer,PheneDummy>> nodePhenes = new Hashtable<Integer,Map<Integer,PheneDummy>>();
		for (NodeGene gene: nodeGenes) {
			int layNum = (int) gene.layerNum;
			if (!layerSet.contains(layNum)) continue;
			if (!nodePhenes.containsKey(layNum)) nodePhenes.put(layNum, new Hashtable<Integer,PheneDummy>());
			Map<Integer,PheneDummy> layerMap = nodePhenes.get(layNum);
			int nodeNum = (int) gene.nodeNum;
			if (!layerMap.containsKey(nodeNum)) layerMap.put(nodeNum, new PheneDummy());
			layerMap.get(nodeNum).addGene(gene);
		}
		return nodePhenes;
	}
	
	private TreeMap<Integer,TreeMap<Integer,TupleSet>> filterNodes(Map<Integer,Map<Integer,PheneDummy>> nodePhenes) {
		TreeMap<Integer,TreeMap<Integer,TupleSet>> tupleMap = new TreeMap<Integer,TreeMap<Integer,TupleSet>>();
		nodePhenes.forEach((layNum, nodeDummies) -> {
			nodeDummies.entrySet().removeIf((entry) -> entry.getValue().getXprSum() < 0 && layNum != -1);
			Map<Integer,Double> biasMap = new Hashtable<Integer,Double>();
			TreeMap<Integer,TupleSet> nodeTuples = new TreeMap<Integer,TupleSet>();
			nodeDummies.forEach((nodeNum, phene) -> {
				biasMap.put(nodeNum, phene.getWeightedAvg());
				nodeTuples.put(nodeNum, new TupleSet());
			});
			laysAndNodes.put(layNum, biasMap);
			tupleMap.put(layNum,  nodeTuples);
		});
		return tupleMap;
	}
	
//	ConnGene Stuff
	/*
	 * Creates a Map of PheneDummy objects, which represent the phenotype for a given connection, using ConnTuple objects as keys.
	 * Then filters the PheneDummy map based on xprLevel, and uses the remaining connections to populate both the TupleSet objects
	 * in 'tupleMap', and a new Map 'connWeights' with the same ConnTuple keys, but a single Double (representing the connection weight)
	 * as a value.
	 * (PheneDummy is defined below, and more information on its functionality is provided there).
	 */
	private TreeMap<ConnTuple,Double> parseConnGenes(List<ConnGene> connGenes, TreeMap<Integer,TreeMap<Integer,TupleSet>> tupleMap) {
		TreeMap<ConnTuple, PheneDummy> connPhenes = new TreeMap<ConnTuple,PheneDummy>();
		for (ConnGene gene : connGenes) {
			ConnTuple tuple = new ConnTuple(gene);
			if (!validTuple(tuple)) continue;
			if (!connPhenes.containsKey(tuple)) connPhenes.put(tuple, new PheneDummy());
			connPhenes.get(tuple).addGene(gene);
		}
		TreeMap<ConnTuple,Double> connWeights = new TreeMap<ConnTuple,Double>();
		for (Map.Entry<ConnTuple, PheneDummy> entry : connPhenes.entrySet()) {
			boolean valid = entry.getValue().getXprSum() > 0;
			ConnTuple tuple = entry.getKey();
			if (valid) {
				if (tuple.iLay() != 0) tupleMap.get(tuple.iLay()).get(tuple.iNode()).addUp(tuple);
				if (tuple.oLay() != -1) tupleMap.get(tuple.oLay()).get(tuple.oNode()).addDown(tuple);
				connWeights.put(tuple, entry.getValue().getWeightedAvg());
			}
		}
		return connWeights;
	}
	
	/*
	 * checks to make sure a given tuple is valid (IE, the connection doesn't point backwards, and both input and output nodes exist)
	 */
	private boolean validTuple(ConnTuple tup) {
		if (tup.oLay() == 0) return false;
		if (tup.oLay() > 0 && tup.oLay() < tup.iLay()) return false;
		boolean bottomIn = false;
		boolean topOut = false;
		if (tup.iLay() == 0) {
			if (tup.iNode() >= bottomNodes) bottomIn = true;
			else return false;
		}
		if (tup.oLay() == -1) {
			if (tup.oNode() < topNodes) topOut = true;
			else return false;
		}
		if (!bottomIn && (!laysAndNodes.containsKey(tup.iLay()) || !laysAndNodes.get(tup.iLay()).containsKey(tup.iNode()))) return false;
		if (!topOut && (!laysAndNodes.containsKey(tup.oLay()) || !laysAndNodes.get(tup.oLay()).containsKey(tup.oNode()))) return false;
		return true;
	}

//	Orphan Stuff
	/*
	 * Used to remove orphans. An orphan node is a node that either has no connections leading into it,
	 * or leading out of it. An orphan layer is simply a layer with no non-orphan nodes.
	 * Removal of orphans is done at this step to speed up runtime when the Network is eventually built.
	 */
	private void removeOrphans(NavigableSet<ConnTuple> tuples, TreeMap<Integer,TreeMap<Integer,TupleSet>> tupleMap) {
		tupleMap.entrySet().removeIf(entry -> {
			int layNum = entry.getKey();
			if (layNum == -1) return false;
			return isLayerOrphan(tuples, layNum, tupleMap.get(layNum), true);
		});
		tupleMap.descendingMap().entrySet().removeIf(entry -> {
			int layNum = entry.getKey();
			if (layNum == -1) return false;
			return isLayerOrphan(tuples, layNum, tupleMap.get(layNum), false);
		});
		for (TupleSet set : tupleMap.get(-1).values()) set.clean(tuples);
	}
	
	private boolean isLayerOrphan(NavigableSet<ConnTuple> tuples, int layNum, TreeMap<Integer,TupleSet> tupleMap, boolean direction) {
		tupleMap.entrySet().removeIf((entry) -> {
			TupleSet tupleSet = entry.getValue();
			boolean isNodeOrphan = isNodeOrphan(tuples, tupleSet, direction);
			if (isNodeOrphan){
				Set<ConnTuple> otherSet = (direction)? tupleSet.downConns : tupleSet.upConns;
				tuples.removeIf(tuple -> otherSet.contains(tuple));
			}
			return isNodeOrphan;
		});
		return tupleMap.isEmpty();
	}
	
	private boolean isNodeOrphan(NavigableSet<ConnTuple> tuples, TupleSet tupleSet, boolean direction) {
		tupleSet.clean(tuples);
		Set<ConnTuple> set = (direction)? tupleSet.upConns : tupleSet.downConns;
		return set.isEmpty();
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

//	Used for Orphan removal, but created/added to in earlier steps for efficiency
	private class TupleSet {
		Set<ConnTuple> upConns = new HashSet<>();
		Set<ConnTuple> downConns = new HashSet<>();
		
		void addUp(ConnTuple tuple) {
			upConns.add(tuple);
		}
		
		void addDown(ConnTuple tuple) {
			upConns.add(tuple);
		}
		
		void clean(Set<ConnTuple> tuples) {
			upConns.removeIf(tuple -> !tuples.contains(tuple));
		}
	}
}
