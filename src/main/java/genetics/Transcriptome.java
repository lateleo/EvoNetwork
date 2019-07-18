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
		List<FamGene> famGenes = new ArrayList<FamGene>();
		for (Gene gene : poolAllGenes(genome)) {
			if (gene.getClass().equals(LayerGene.class)) layGenes.add((LayerGene) gene);
			else if (gene.getClass().equals(NodeGene.class)) nodeGenes.add((NodeGene) gene);
			else if (gene.getClass().equals(ConnGene.class)) connGenes.add((ConnGene) gene);
			else if (gene.getClass().equals(FamGene.class)) famGenes.add((FamGene) gene);
		}
		Set<Integer> layerSet = parseLayGenes(layGenes);
		TreeMap<Integer,TreeMap<Integer,TupleSet>> tupleMap = parseNodeGenes(nodeGenes, layerSet);
		TreeMap<ConnTuple,List<Triplet>> tripletMap = parseConnGenes(connGenes, tupleMap);
		removeOrphans(tripletMap.navigableKeySet(), tupleMap);
		parseFamGenes(famGenes, tripletMap);
	}
	
	public Map<Integer, Map<Integer, Double>> getLaysAndNodes() {
		return laysAndNodes;
	}

	public Map<ConnTuple, Double> getConnWeights() {
		return connWeights;
	}
	
	private List<Gene> poolAllGenes(Genome genome) {
		List<Gene> genes = new ArrayList<Gene>();
		Consumer<HomologPair> chromConsumer = (pair) -> genes.addAll(pair.getGenes());
		genome.forEach(chromConsumer);
		return genes;
	}
	
//	LayerGene Stuff
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
	private TreeMap<ConnTuple,List<Triplet>> parseConnGenes(List<ConnGene> connGenes,
			TreeMap<Integer,TreeMap<Integer,TupleSet>> tupleMap) {
		TreeMap<ConnTuple,List<Triplet>> tripletMap = new TreeMap<ConnTuple,List<Triplet>>();
		for (ConnGene gene : connGenes) {
			ConnTuple tuple = new ConnTuple(gene);
			if (!validTuple(tuple)) continue;
			Triplet triplet = new Triplet(gene);
			if (tripletMap.containsKey(tuple)) tripletMap.get(tuple).add(triplet);
			else tripletMap.put(tuple, new ArrayList<>(Arrays.asList(triplet)));
		}
		Predicate<Map.Entry<ConnTuple, List<Triplet>>> filter = (entry) -> {
			boolean valid = entry.getValue().stream().mapToDouble((trip) -> trip.xprLevel).sum() > 0;
			ConnTuple tuple = entry.getKey();
			if (valid) {
				if (tuple.iLay() != 0) tupleMap.get(tuple.iLay()).get(tuple.iNode()).addUp(tuple);
				if (tuple.oLay() != -1) tupleMap.get(tuple.oLay()).get(tuple.oNode()).addDown(tuple);
			}
			return !valid;
		};
		tripletMap.entrySet().removeIf(filter);
		return tripletMap;
	}
	
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
	
//	FamGene Stuff
	private void parseFamGenes(List<FamGene> famGenes, Map<ConnTuple,List<Triplet>> tripletMap) {
		Map<Integer,PheneDummy> pheneMap = fillFams(famGenes);
		Map<Integer,Double> famWeights = filterFams(pheneMap);
		List<Triplet> tripletList = new ArrayList<Triplet>();
		tripletMap.values().forEach((list)-> tripletList.addAll(list));
		applyFamWeights(famWeights, tripletList);
		combineTriplets(tripletMap);
	}
	
	private Map<Integer,PheneDummy> fillFams(List<FamGene> famGenes) {
		Map<Integer,PheneDummy> pheneMap = new Hashtable<Integer,PheneDummy>();
		for (FamGene gene : famGenes) {
			int sign = gene.signFilter;
			if (!pheneMap.containsKey(sign)) pheneMap.put(sign, new PheneDummy());
			pheneMap.get(sign).addGene(gene);
		}
		return pheneMap;
	}
	
	private Map<Integer,Double> filterFams(Map<Integer,PheneDummy> pheneMap) {
		Map<Integer,Double> famWeights = new Hashtable<Integer,Double>();
		pheneMap.entrySet().removeIf((entry)-> entry.getValue().getXprSum() < 0);
		pheneMap.forEach((sign, dummy) -> famWeights.put(sign, dummy.getWeightedAvg()));
		return famWeights;
	}
	
	private void applyFamWeights(Map<Integer,Double> famWeights, List<Triplet> tripletList) {
		famWeights.forEach((signFilter, weight) -> {
			List<Triplet> matches = new ArrayList<Triplet>(tripletList);
			matches.removeIf((triplet) -> triplet.match(signFilter));
			matches.forEach((triplet) -> triplet.addWeight(weight));
		});
	}
	
	private void combineTriplets(Map<ConnTuple,List<Triplet>> tripletMap) {
		tripletMap.forEach((tuple, triplets) -> {
			double xprSum = triplets.stream().mapToDouble((triplet) -> triplet.xprLevel).sum();
			double finalWeight = triplets.stream().mapToDouble((triplet) -> triplet.getFinalWeight()).sum()/xprSum;
			connWeights.put(tuple, finalWeight);
		});
	}
	
//	HELPER CLASSES
	
//	Used briefly in parsing NodeGenes.
	private class PheneDummy {
		List<Double> xprVals = new ArrayList<Double>();
		List<Pair<Double, Double>> valPairs = new ArrayList<Pair<Double,Double>>();
		
		void addGene(NodeGene gene) {
			xprVals.add(gene.xprLevel);
			valPairs.add(new Pair<Double,Double>(gene.xprLevel, gene.bias));
		}
		
		void addGene(FamGene gene) {
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
	
//	Used in ConnGene and FamGene parsing, eventually replaced by single weight value
	private class Triplet {
		int sign;
		double weight, xprLevel;
		
		Triplet(ConnGene gene) {
			this.sign = gene.signature;
			this.weight = gene.weight;
			this.xprLevel = gene.xprLevel;
		}
		
		void addWeight(double weight) {
			this.weight += weight;
		}
		
		double getFinalWeight() {
			return weight*xprLevel;
		}
		
		boolean match(int signFilter) {
			return (sign & signFilter) > 0L;
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
