package ecology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import data.MnistDataReader;
import data.MnistImage;
import genetics.Chromosome;
import genetics.ConnGene;
import genetics.Gene;
import genetics.Genome;
import genetics.HaploidSet;
import genetics.HomologPair;
import genetics.LayerGene;
import genetics.NodeGene;
import genetics.RegGene;
import network.Organism;
import staticUtils.CMUtils;
import staticUtils.ComparisonUtils;
import staticUtils.MathUtils;
import staticUtils.RNG;
import utils.ConnTuple;
import utils.NodeTuple;
import utils.NodeVector;

/*
 * This class is mostly a variable dump and simulation setup class. It doesn't do anything, After creating
 * the initial generation of organisms, except hold the starting parameters that we may want to tweak between runs,
 * so they can be referenced here and there by various classes that need them.
 */
public abstract class Species {
	public static MnistDataReader mnistReader;
	public static MnistImage[][] images;
	public static MnistImage[] testImages;
	public static int bottomWidth;
	public static int minBottom;
	public static int maxBottom;
	public static int topNodes;
	
	public static double dormancy;

	public static int batchSize, haploidNum, layers, nodes, conns, topRedundancy, bottomRedundancy;

	public static int populationSize;
	public static int simulatedGenerations;

	public static void initialize(String dataset, int batchSize) {
		Species.batchSize = batchSize;
		mnistReader = new MnistDataReader(dataset);
		try {
			testImages = mnistReader.readTestingData();
			MnistImage[] allImages = mnistReader.readTrainingData();
			int imgCount = allImages.length;
			int batches = imgCount / batchSize;
			MnistImage[][] sortedImages = new MnistImage[batches][batchSize];
			for (int i = 0; i < imgCount; i++) {
				int a = i / batchSize;
				int b = i % batchSize;
				sortedImages[a][b] = allImages[i];
			}
			images = sortedImages;
		} catch (IOException e) {
			e.printStackTrace();
		}
		minBottom = -bottomWidth/2;
		maxBottom = bottomWidth + minBottom;
	}

	public static Population createPopulation(double mRateFactor, double mMagFactor, double slipFactor, int haploidNum,
			int layers, int nodes, int conns, int topRedundancy, int bottomRedundancy, double dormancy, int popSize, int simGens) {

		Species.populationSize = popSize;
		Species.simulatedGenerations = simGens;

		Species.haploidNum = haploidNum;
		Species.layers = layers;
		Species.nodes = nodes;
		Species.conns = conns;
		Species.topRedundancy = topRedundancy;
		Species.bottomRedundancy = bottomRedundancy;
		Species.dormancy = dormancy;
		
		Set<Integer> activeLayNums = generateActiveLayNums();
		Set<Integer> dormantLayNums = generateDormantLayNums(activeLayNums);
		Set<NodeTuple> activeNodesA = generateActiveNodeVectors(activeLayNums);
		Set<NodeTuple> activeNodesD = generateActiveNodeVectors(dormantLayNums);
		Set<NodeTuple> dormantNodesA = generateDormantNodeVectors(activeLayNums, activeNodesA);
		Set<NodeTuple> dormantNodesD = generateDormantNodeVectors(dormantLayNums, activeNodesD);
		Set<NodeTuple> allInactiveNodes = new TreeSet<>(activeNodesD);
		allInactiveNodes.addAll(dormantNodesA);
		allInactiveNodes.addAll(dormantNodesD);
		
		Set<NodeTuple> bottomTuples = generateBottomTuples();
		Set<NodeTuple> topTuples = generateTopTuples();
		
		Set<ConnTuple> activeConns = generateActiveBottomConns(bottomTuples, activeNodesA, false);
		activeConns.addAll(generateActiveBottomConns(bottomTuples, allInactiveNodes, true));
		activeConns.addAll(generateActiveTopConns(topTuples, activeNodesA, false));
		activeConns.addAll(generateActiveTopConns(topTuples, allInactiveNodes, true));
		
		Set<ConnTuple> dormantConns = generateDormantBottomConns(bottomTuples, activeNodesA, activeConns, false);
		dormantConns.addAll(generateDormantBottomConns(bottomTuples, allInactiveNodes, activeConns, true));
		dormantConns.addAll(generateDormantTopConns(topTuples, activeNodesA, activeConns, false));
		dormantConns.addAll(generateDormantTopConns(topTuples, allInactiveNodes, activeConns, true));
		
		Set<ConnTuple> allConnTuples = new TreeSet<>(activeConns);
		allConnTuples.addAll(dormantConns);

		
		Set<NodeTuple> sampleSet1 = new TreeSet<>(activeNodesA);
		sampleSet1.addAll(bottomTuples);
		sampleSet1.addAll(topTuples);
		Set<NodeTuple> sampleSet2 = new TreeSet<>(sampleSet1);
		sampleSet2.addAll(activeNodesD);
		sampleSet2.addAll(dormantNodesA);
		Set<NodeTuple> sampleSet3 = new TreeSet<>(sampleSet2);
		sampleSet3.addAll(dormantNodesD);		

		activeConns.addAll(generateMidConns(activeNodesA, sampleSet1, allConnTuples, false));
		activeConns.addAll(generateMidConns(activeNodesD, sampleSet2, allConnTuples, false));
		activeConns.addAll(generateMidConns(dormantNodesA, sampleSet2, allConnTuples, false));
		activeConns.addAll(generateMidConns(dormantNodesD, sampleSet3, allConnTuples, false));

		dormantConns.addAll(generateMidConns(activeNodesA, sampleSet1, allConnTuples, true));
		dormantConns.addAll(generateMidConns(activeNodesD, sampleSet2, allConnTuples, true));
		dormantConns.addAll(generateMidConns(dormantNodesA, sampleSet2, allConnTuples, true));
		dormantConns.addAll(generateMidConns(dormantNodesD, sampleSet3, allConnTuples, true));
		
		List<Gene> genes = new ArrayList<>();
		genes.addAll(generateLayerGenes(activeLayNums, dormantLayNums));
		genes.addAll(generateNodeGenes(activeNodesA, activeNodesD, dormantNodesA, dormantNodesD, topTuples));
		genes.addAll(generateConnGenes(activeConns, false));
		genes.addAll(generateConnGenes(dormantConns, true));
		Collections.shuffle(genes);
		
		List<Chromosome> haploidPrototype = generateHaploidPrototype(genes);
		for (Chromosome chrom : haploidPrototype) {
			chrom.insert(new RegGene(true, mRateFactor, mMagFactor, slipFactor));
			chrom.insert(new RegGene(false, mRateFactor, mMagFactor, slipFactor));
		}
		Map<Integer,List<Chromosome>> sisterChromatids = generateSisterChromatids(haploidPrototype, mMagFactor, popSize);
		List<Organism> orgs = generateOrganisms(sisterChromatids, popSize);

		Population.setInstance(orgs);
		return Population.getInstance();
	}
	
	private static Set<Integer> generateActiveLayNums() {
		Set<Integer> layNums = new TreeSet<>();
		while (layNums.size() < layers) layNums.add(RNG.getIntRange(1, 4*layers));
		return layNums;
	}
	
	private static Set<Integer> generateDormantLayNums(Set<Integer> activeLayNums) {
		Set<Integer> layNums = new TreeSet<>();
		int max = (int) (4*layers*(1+dormancy));
		while (layNums.size() < layers*dormancy) {
			int rand = RNG.getIntRange(1, max);
			if (!activeLayNums.contains(rand)) layNums.add(rand);
		}
		return layNums;
	}
	
	private static Set<NodeTuple> generateActiveNodeVectors(Set<Integer> layNums) {
		Set<NodeTuple> nodeTuples = new TreeSet<>();
		for (Integer layNum : layNums) {
			Set<NodeVector> vectors = new TreeSet<NodeVector>();
			int bound = (int) Math.ceil(nodes*0.5);
			while (vectors.size() < nodes) {
				double x = RNG.getIntRange(-bound, bound);
				double y = RNG.getIntRange(-bound, bound);
				vectors.add(new NodeVector(x, y));
			}
			for (NodeVector vector : vectors) nodeTuples.add(new NodeTuple(layNum, vector));
		}
		return nodeTuples;
	}
	
	private static Set<NodeTuple> generateDormantNodeVectors(Set<Integer> layNums, Set<NodeTuple> activeNodes) {
		Set<NodeTuple> nodeTuples = new TreeSet<>();
		for (Integer layNum : layNums) {
			Set<NodeTuple> tuples = new TreeSet<NodeTuple>();
			int bound = (int) Math.ceil(nodes*0.5);
			int max = (int) Math.ceil(nodes*dormancy);
			while (tuples.size() < max) {
				double x = RNG.getIntRange(-bound, bound);
				double y = RNG.getIntRange(-bound, bound);
				NodeTuple tuple = new NodeTuple(layNum, new NodeVector(x, y));
				if (!activeNodes.contains(tuple)) tuples.add(tuple);
			}
			nodeTuples.addAll(tuples);
		}
		return nodeTuples;
	}
	
	private static Set<NodeTuple> generateBottomTuples() {
		Set<NodeTuple> bottomTuples = new TreeSet<>();
		for (NodeVector vector : NodeVector.bottomVectors) bottomTuples.add(new NodeTuple(0, vector));
		return bottomTuples;
	}
	
	private static Set<NodeTuple> generateTopTuples() {
		Set<NodeTuple> topTuples = new TreeSet<>();
		for (NodeVector vector : NodeVector.unitVectors) {
			double phi = Math.PI/topNodes;
			NodeVector topVector = vector.clone().addTheta(phi).addMagnitude(topNodes);
			topTuples.add(new NodeTuple(-1, topVector));
		}
		return topTuples;
	}
	
	private static Set<ConnTuple> generateActiveBottomConns(Set<NodeTuple> bottomTuples, Set<NodeTuple> outputNodes, boolean dormant) {
		Set<ConnTuple> bottomConns = new TreeSet<>();
		List<NodeTuple> outputList = new ArrayList<>(outputNodes);
//		int sampleSize = (dormant)? (int) Math.ceil(bottomRedundancy*dormancy) : bottomRedundancy;
		int sampleSize = bottomRedundancy;
		for (NodeTuple bottom : bottomTuples) {
			for (NodeTuple output : RNG.getSample(outputList, sampleSize)) {
				bottomConns.add(new ConnTuple(bottom, output));
			}
		}
		return bottomConns;
	}
	
	private static Set<ConnTuple> generateDormantBottomConns(Set<NodeTuple> bottomTuples, Set<NodeTuple> outputNodes, Set<ConnTuple> activeConns, boolean dormant) {
		Set<ConnTuple> bottomConns = new TreeSet<>();
		List<NodeTuple> outputList = new ArrayList<>(outputNodes);
//		int ceil = (int) Math.ceil(bottomRedundancy*((dormant) ? dormancy*dormancy : dormancy));
		int ceil = (int) Math.ceil(bottomRedundancy*dormancy);
		for (NodeTuple bottom : bottomTuples) {
			Set<ConnTuple> newConns = new TreeSet<>();
			while (newConns.size() < ceil) {
				ConnTuple conn = new ConnTuple(bottom, RNG.sampleList(outputList));
				if (!activeConns.contains(conn)) newConns.add(conn);
			}
			bottomConns.addAll(newConns);
		}
		return bottomConns;
	}
	
	private static Set<ConnTuple> generateActiveTopConns(Set<NodeTuple> topTuples, Set<NodeTuple> inputNodes, boolean dormant) {
		Set<ConnTuple> topConns = new TreeSet<>();
		List<NodeTuple> inputList = new ArrayList<>(inputNodes);
//		int sampleSize = (dormant)? (int) Math.ceil(topRedundancy*dormancy) : topRedundancy;
		int sampleSize = topRedundancy;
		for (NodeTuple top : topTuples) {
			for (NodeTuple output : RNG.getSample(inputList, sampleSize)) {
				topConns.add(new ConnTuple(output, top));
			}
		}
		return topConns;
	}
	
	private static Set<ConnTuple> generateDormantTopConns(Set<NodeTuple> topTuples, Set<NodeTuple> inputNodes, Set<ConnTuple> activeConns, boolean dormant) {
		Set<ConnTuple> topConns = new TreeSet<>();
		List<NodeTuple> inputList = new ArrayList<>(inputNodes);
//		int ceil = (int) Math.ceil(topRedundancy*((dormant) ? dormancy*dormancy : dormancy));
		int ceil = (int) Math.ceil(dormancy*topRedundancy);
		for (NodeTuple top : topTuples) {
			Set<ConnTuple> newConns = new TreeSet<>();
			while (newConns.size() < ceil) {
				ConnTuple conn = new ConnTuple(RNG.sampleList(inputList), top);
				if (!activeConns.contains(conn)) newConns.add(conn);
			}
			topConns.addAll(newConns);
		}
		return topConns;
	}
	
	private static Set<ConnTuple> generateMidConns(Set<NodeTuple> iterSet, Set<NodeTuple> refSet, Set<ConnTuple> existingConns, boolean dormant) {
		Set<ConnTuple> connSet = new TreeSet<>();
		List<NodeTuple> refList = new ArrayList<>(refSet);
		int ceil = (dormant) ? (int) Math.ceil(conns*dormancy) : conns;
		for (NodeTuple iterNode : iterSet) {
			Set<ConnTuple> newConns = new TreeSet<>();
			while (newConns.size() < ceil) {
				NodeTuple refNode = RNG.sampleList(refList);
				while (refNode.layer() == iterNode.layer()) refNode = RNG.sampleList(refList);
				boolean up = ComparisonUtils.compareLayNums(refNode.layer(), iterNode.layer()) > 0;
				NodeTuple nodeA = (up) ? iterNode : refNode;
				NodeTuple nodeB = (up) ? refNode : iterNode;
				ConnTuple conn = new ConnTuple(nodeA, nodeB);
				if (!connSet.contains(conn) && !existingConns.contains(conn)) newConns.add(new ConnTuple(nodeA, nodeB));
			}
			connSet.addAll(newConns);
			existingConns.addAll(newConns);
		}
		return connSet;
	}

	private static List<LayerGene> generateLayerGenes(Set<Integer> activeLayers, Set<Integer> dormantLayers) {
		List<LayerGene> layGenes = new ArrayList<>();
		for (Integer layNum : activeLayers) {
			for (int i = 0; i < 4; i++) layGenes.add(new LayerGene(true, true, layNum));
		}
		for (Integer layNum : dormantLayers) {
			for (int i = 0; i < 2; i++) {
				layGenes.add(new LayerGene(false, true, layNum));
				layGenes.add(new LayerGene(false, false, layNum));
			}
		}
		return layGenes;
	}
	
	private static List<NodeGene> generateMidNodeGenes(Set<NodeTuple> tuples, boolean dormant) {
		List<NodeGene> nodeGenes = new ArrayList<>();
		for (NodeTuple tuple : tuples) {
			for (int i = 0; i < 2; i++) {
				nodeGenes.add(new NodeGene(!dormant, true, tuple));
				nodeGenes.add(new NodeGene(!dormant, true, tuple));
			}
		}
		return nodeGenes;
	}
	
	private static List<NodeGene> generateNodeGenes(Set<NodeTuple> activeA, Set<NodeTuple> activeD, Set<NodeTuple> dormantA, Set<NodeTuple> dormantD, Set<NodeTuple> tops) {
		List<NodeGene> nodeGenes = new ArrayList<>();
		nodeGenes.addAll(generateMidNodeGenes(activeA, false));
		nodeGenes.addAll(generateMidNodeGenes(activeD, false));
		nodeGenes.addAll(generateMidNodeGenes(dormantA, true));
		nodeGenes.addAll(generateMidNodeGenes(dormantD, true));
		for (NodeTuple tuple : tops) {
			for (int i = 0; i < 10; i++) {
				nodeGenes.add(new NodeGene(true, true, tuple));
			}
		}
		return nodeGenes;
	}
	
	private static List<ConnGene> generateConnGenes(Set<ConnTuple> connTuples, boolean dormant) {
		List<ConnGene> connGenes = new ArrayList<>();
		for (ConnTuple tuple : connTuples) {
			connGenes.add(new ConnGene(!dormant, true, tuple));
			connGenes.add(new ConnGene(!dormant, true, tuple));
		}
		return connGenes;
	}
	
	private static List<Chromosome> generateHaploidPrototype(List<Gene> genes) {
		List<Chromosome> chromosomes = new ArrayList<>();
		for (int i = 1; i <= haploidNum; i++) chromosomes.add(new Chromosome(i));
		for (Gene gene : genes) RNG.sampleList(chromosomes).append(gene);
		return chromosomes;
	}
	
	private static Map<Integer, List<Chromosome>> generateSisterChromatids(List<Chromosome> haploidPrototype, double mMagFactor, int popSize) {
		Map<Integer,List<Chromosome>> sisterChromatids = new TreeMap<>();
		double mag = MathUtils.hyperbolicSquish(mMagFactor);
		for (Chromosome chrom : haploidPrototype) {
			List<Chromosome> copies = new ArrayList<>();
			Chromosome current = chrom;
			for (int i = 0; i < popSize; i++) {
				Chromosome copy = current.copyAndMutate(mag);
				copies.add(copy);
				current = copy;
			}
			sisterChromatids.put(chrom.chromNum(), copies);
		}
		return sisterChromatids;
	}
	
	private static List<Organism> generateOrganisms(Map<Integer, List<Chromosome>> sisterChromatids, int popSize) {
		List<Organism> orgs = new ArrayList<>();
		for (int i = 0; i < popSize; i++) {
			List<HomologPair> homologPairs = new ArrayList<>();
			for (List<Chromosome> chromatids : sisterChromatids.values()) {
				Chromosome a = chromatids.get(i);
				Chromosome b = chromatids.get((i+1) % popSize);
				homologPairs.add(new HomologPair(a, b));
			}
			Genome genome = new Genome(homologPairs);
			Organism org = new Organism(genome);
			genome.setOrg(org);
			orgs.add(org);
		}
		return orgs;
	}

}
