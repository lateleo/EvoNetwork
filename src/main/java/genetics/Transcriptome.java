package genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import ecology.Species;
import staticUtils.ComparisonUtils;
import utils.ConnTuple;
import utils.NodeVector;

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
	private static int bottomNodes = Species.bottomWidth;
	private static int topNodes = Species.topNodes;
	private static Comparator<Integer> comparator = ComparisonUtils::compareLayNums;

	private TreeMap<Integer, TreeMap<NodeVector, NodePhene>> laysAndNodes = new TreeMap<>(comparator);
	private TreeMap<ConnTuple, Double> connWeights = new TreeMap<>();

	/*
	 * Public getter for the 'laysAndNodes' map
	 */
	public TreeMap<Integer, TreeMap<NodeVector, NodePhene>> getLaysAndNodes() {
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
		removeOrphans();
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
		for (Map.Entry<Integer, List<Double>> entry : layerMap.entrySet()) {
			if (entry.getValue().stream().mapToDouble((d) -> d).sum() >= 0.0) {
				laysAndNodes.put(entry.getKey(), new TreeMap<NodeVector,NodePhene>());
			}
		}
		laysAndNodes.put(-1, new TreeMap<>());
	}
	
//	NodeGene Stuff
	private void fillNodes(List<NodeGene> nodeGenes) {
		Map<NodeVector,NodePhene> topMap = laysAndNodes.get(-1);
		for (NodeVector vector : NodeVector.unitVectors) topMap.put(vector, new NodePhene());
		for (NodeGene gene: nodeGenes) {
			int layNum = (int) Math.floor(gene.layerNum);
			if (!laysAndNodes.containsKey(layNum)) continue;
			Map<NodeVector,NodePhene> pheneMap = laysAndNodes.get(layNum);
			NodeVector vector;
			if (layNum == -1) {
				vector = gene.nodeVector.getUnitVector();
			} else {
				vector = gene.nodeVector.snapToGrid();
				if (!pheneMap.containsKey(vector)) pheneMap.put(vector, new NodePhene());
			}
			pheneMap.get(vector).addGene(gene);
		}
	}
	
	private void filterNodes() {
		for (Map.Entry<Integer,TreeMap<NodeVector,NodePhene>> layer : laysAndNodes.entrySet()) {
			int layNum = layer.getKey();
			TreeMap<NodeVector,NodePhene> pheneMap = layer.getValue();
			if (layNum != -1) pheneMap.entrySet().removeIf((entry) -> entry.getValue().getXprSum() < 0.0);
		}

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
		TreeMap<ConnTuple,ConnPhene> connPhenes = new TreeMap<ConnTuple,ConnPhene>();
		for (ConnGene gene : connGenes) {
			ConnTuple connTuple = new ConnTuple(gene);
			if (!isConnTupleValid(connTuple)) continue;
			if (!connPhenes.containsKey(connTuple)) connPhenes.put(connTuple, new ConnPhene());
			connPhenes.get(connTuple).addGene(gene);
		}
		for (Map.Entry<ConnTuple,ConnPhene> entry : connPhenes.entrySet()) {
			ConnTuple tuple = entry.getKey();
			ConnPhene connPhene = entry.getValue();
			if (connPhene.getXprSum() > 0) {
				if (tuple.iLay() != 0) laysAndNodes.get(tuple.iLay()).get(tuple.iNode()).addUp(tuple);
				laysAndNodes.get(tuple.oLay()).get(tuple.oNode()).addDown(tuple);
				connWeights.put(tuple, connPhene.getWeight());
			}
		}
	}
	
	/*
	 * checks to make sure a given tuple is valid (IE, the connection doesn't point backwards, and both input and output nodes exist)
	 */
	private boolean isConnTupleValid(ConnTuple tuple) {
		int iLay = tuple.iLay();
		int oLay = tuple.oLay();
		NodeVector iNode = tuple.iNode();
		NodeVector oNode = tuple.oNode();
		if (comparator.compare(iLay, oLay) >= 0) return false;
		boolean iValid = false;
		boolean oValid = false;
		if (iLay == 0) {
			double x = iNode.getX();
			double y = iNode.getY();
			if (x < bottomNodes && x >= 0 && y < bottomNodes && y >= 0) iValid = true;
			else return false;
		}
		if (oLay == -1) oValid = true;
		if (!iValid) {
			if (!laysAndNodes.containsKey(iLay)) return false;
			else if (!laysAndNodes.get(iLay).containsKey(iNode)) return false;
			else iValid = true;
		}
		if (!oValid) {
			if (!laysAndNodes.containsKey(oLay)) return false;
			else if (!laysAndNodes.get(oLay).containsKey(oNode)) return false;
			else oValid = true;
		}
		return iValid && oValid;
	}

//	Orphan Stuff
	/*
	 * Used to remove orphans. An orphan node is a node that either has no connections leading into it,
	 * or leading out of it. An orphan layer is simply a layer with no non-orphan nodes.
	 * Removal of orphans is done at this step to speed up runtime when the Network is eventually built.
	 */
	private void removeOrphans() {
		laysAndNodes.entrySet().removeIf(entry -> isLayerOrphan(entry, true));
		laysAndNodes.descendingMap().entrySet().removeIf(entry -> isLayerOrphan(entry, false));
		for (NodePhene phene : laysAndNodes.get(-1).values()) phene.filterConns(connWeights.navigableKeySet(), true);
	}
	
	private boolean isLayerOrphan(Map.Entry<Integer,TreeMap<NodeVector,NodePhene>> entry, boolean direction) {
		if (entry.getKey() == -1) return false;
		TreeMap<NodeVector,NodePhene> pheneMap = entry.getValue();
		pheneMap.values().removeIf((phene) -> phene.isOrphan(connWeights.navigableKeySet(), direction));
		return pheneMap.isEmpty();
	}

}
