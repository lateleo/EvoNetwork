package genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import ecology.Species;
import network.Conn;
import utils.ConnTuple;

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
	

	private TreeMap<Integer, TreeMap<Integer, NodePhene>> laysAndNodes = new TreeMap<>(comparator);
	private TreeMap<ConnTuple, Double> connWeights = new TreeMap<>();

	/*
	 * Public getter for the 'laysAndNodes' map
	 */
	public TreeMap<Integer, TreeMap<Integer, NodePhene>> getLaysAndNodes() {
		return laysAndNodes;
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

		parseLayGenes(layGenes);
		fillNodes(nodeGenes);
		filterNodes();
		parseConnGenes(connGenes);
		removeOrphans(connWeights.navigableKeySet());
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
	private void parseLayGenes(List<LayerGene> layGenes) {
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
		layerMap.forEach((layNum,xprVals) -> {
			if (xprVals.stream().mapToDouble((d) -> d).sum() >= 0.0) {
				laysAndNodes.put(layNum, new TreeMap<Integer,NodePhene>());
			}
		});
		laysAndNodes.put(-1, new TreeMap<Integer,NodePhene>());
	}
	
//	NodeGene Stuff
	private void fillNodes(List<NodeGene> nodeGenes) {
		for (NodeGene gene: nodeGenes) {
			int layNum = (int) Math.floor(gene.layerNum);
			if (!laysAndNodes.containsKey(layNum)) continue;
			Map<Integer,NodePhene> pheneMap = laysAndNodes.get(layNum);
			int nodeNum = (int) gene.nodeNum;
			if (!pheneMap.containsKey(nodeNum)) pheneMap.put(nodeNum, new NodePhene());
			pheneMap.get(nodeNum).addGene(gene);
		}
	}
	
	private void filterNodes() {
		laysAndNodes.forEach((layNum, pheneMap) -> {
			if (layNum == -1) {
				for (int nodeNum = 0; nodeNum < topNodes; nodeNum++) {
					if (!pheneMap.containsKey(nodeNum)) pheneMap.put(nodeNum, new NodePhene());
				}
			}
			else {
				pheneMap.entrySet().removeIf((entry) -> entry.getValue().getXprSum() < 0.0);
				pheneMap.forEach((nodeNum, phene) -> phene.setBias());
			}
		});
	}
	
//	ConnGene Stuff
	/*
	 * Creates a Map of PheneDummy objects, which represent the phenotype for a given connection, using ConnTuple objects as keys.
	 * Then filters the PheneDummy map based on xprLevel, and uses the remaining connections to populate both the TupleSet objects
	 * in 'tupleMap', and a new Map 'connWeights' with the same ConnTuple keys, but a single Double (representing the connection weight)
	 * as a value.
	 * (PheneDummy is defined below, and more information on its functionality is provided there).
	 */
	private void parseConnGenes(List<ConnGene> connGenes) {
		TreeMap<ConnTuple, ConnPhene> connPhenes = new TreeMap<ConnTuple,ConnPhene>();
		for (ConnGene gene : connGenes) {
			ConnTuple connTuple = new ConnTuple(gene);
			if (!isConnTupleValid(connTuple)) continue;
			if (!connPhenes.containsKey(connTuple)) connPhenes.put(connTuple, new ConnPhene());
			connPhenes.get(connTuple).addGene(gene);
		}
		connPhenes.forEach((tuple,connPhene) -> {
			if (connPhene.getXprSum() > 0) {
				if (tuple.iLay() != 0) laysAndNodes.get(tuple.iLay()).get(tuple.iNode()).addUp(tuple);
				TreeMap<Integer,NodePhene> pheneMap = laysAndNodes.get(tuple.oLay());
				NodePhene nodePhene = pheneMap.get(tuple.oNode());
				nodePhene.addDown(tuple);
				connWeights.put(tuple, connPhene.getWeightedAvg());
			}
		});
	}
	
/*	private void loadReasons() {
		validityMap.put("Top/Bottom in wrong position: ", new TreeSet<>());
		validityMap.put("Input Layer Matches Output: ", new TreeSet<>());
		validityMap.put("Bottom NodeNum > " + bottomNodes + ": \t", new TreeSet<>());
		validityMap.put("Top NodeNum > " + topNodes + ": \t", new TreeSet<>());
		validityMap.put("Tuple pointing downwards: ", new TreeSet<>());
		validityMap.put("Non-Existent Input Layer: ", new TreeSet<>());
		validityMap.put("Non-Existent Input Node: ", new TreeSet<>());
		validityMap.put("Non-Existent Output Layer: ", new TreeSet<>());
		validityMap.put("Non-Existent Output Node: ", new TreeSet<>());
		validityMap.put("Valid: \t\t\t", new TreeSet<>());
	}
	*/
	
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
				if (!laysAndNodes.containsKey(tuple.iLay())) return false;
				else if (!laysAndNodes.get(tuple.iLay()).containsKey(tuple.iNode())) return false;
				else iValid = true;
			}
			if (!oValid) {
				if (!laysAndNodes.containsKey(tuple.oLay())) return false;
				else if (!laysAndNodes.get(tuple.oLay()).containsKey(tuple.oNode())) return false;
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
		laysAndNodes.entrySet().removeIf(entry -> {
			int layNum = entry.getKey();
			if (layNum == -1) return false;
			return isLayerOrphan(connTuples, laysAndNodes.get(layNum), true);
		});
		laysAndNodes.descendingMap().entrySet().removeIf(entry -> {
			int layNum = entry.getKey();
			if (layNum == -1) return false;
			return isLayerOrphan(connTuples, laysAndNodes.get(layNum), false);
		});
		for (NodePhene setPair : laysAndNodes.get(-1).values()) setPair.downConns.retainAll(connTuples);
	}
	
	private boolean isLayerOrphan(NavigableSet<ConnTuple> connTuples, TreeMap<Integer,NodePhene> pheneMap, boolean direction) {
		pheneMap.entrySet().removeIf((entry) -> {
			NodePhene nodePhene = entry.getValue();
			Set<ConnTuple> connSet = (direction)? nodePhene.downConns : nodePhene.upConns;
			connSet.retainAll(connTuples);
			boolean orphanNode = connSet.isEmpty();
			if (orphanNode){
				Set<ConnTuple> otherSet = (direction)? nodePhene.upConns : nodePhene.downConns;
				connTuples.removeAll(otherSet);
			}
			return orphanNode;
		});
		return pheneMap.isEmpty();
	}

}
