package ecology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import genetics.HomologPair;
import genetics.LayerGene;
import genetics.NodeGene;
import network.Organism;
import utils.CMUtils;
import utils.ConnTuple;
import utils.RNG;

/*
 * This class is mostly a variable dump and simulation setup class. It doesn't do anything, After creating
 * the initial generation of organisms, except hold the starting parameters that we may want to tweak between runs,
 * so they can be referenced here and there by various classes that need them.
 */
public abstract class Species {
	public static MnistDataReader mnistReader;
	public static MnistImage[][] images;
	public static MnistImage[] testImages;
	public static int bottomNodes;
	public static int topNodes;

	public static double mutationRate;
	public static double mutationMagnitude;
	public static double slipFactor;
	public static double learningRate;

	public static int batchSize, haploidNum, layers, nodes, conns, topRedundancy, bottomRedundancy;

	public static int populationSize;
	public static int simulatedGenerations;

	public static Comparator<Integer> comparator = (int1, int2) -> {
		if (int1 == int2)
			return 0;
		if (int1 == -1)
			return 1;
		if (int2 == -1)
			return -1;
		else
			return int1 - int2;
	};

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

	}

	public static Population createPopulation(double mRate, double mMag, double slip, double lRate, int haploidNum,
			int layers, int nodes, int conns, int topRedundancy, int bottomRedundancy, int popSize, int simGens) {

		Species.mutationRate = mRate;
		Species.mutationMagnitude = mMag;
		Species.slipFactor = slip;
		Species.learningRate = lRate;
		Species.populationSize = popSize;
		Species.simulatedGenerations = simGens;

		Species.haploidNum = haploidNum;
		Species.layers = layers;
		Species.nodes = nodes;
		Species.conns = conns;
		Species.topRedundancy = topRedundancy;
		Species.bottomRedundancy = bottomRedundancy;

//		System.out.println("Generating Genes...");
		List<Gene> genes = generateGenes();
//		System.out.println("Filling Chromosomes...");
		Chromosome[] chromosomes = fillChromosomes(genes);
//		System.out.println("Generating Homologs...");
		Map<Integer, List<HomologPair>> homologPairs = generateHomologs(chromosomes);
//		System.out.println("Creating First Generation...");
		List<Organism> orgs = getFirstGen(homologPairs);
		return Population.setInstance(orgs);
	}

	public static List<Gene> generateGenes() {
		ArrayList<Gene> genes = new ArrayList<>();
		Map<Integer, Set<Integer>> layNodeNums = new TreeMap<>(comparator);

		genes.addAll(generateLayerGenes(layNodeNums));

		genes.addAll(generateNodeGenes(layNodeNums));
		
		genes.addAll(generateConnGenes(layNodeNums));

		Collections.shuffle(genes);
		return genes;
	}

	/* Method used at the beginning of the simulation to generate a list of
	 * genes. */
	public static ArrayList<LayerGene> generateLayerGenes(Map<Integer, Set<Integer>> layNodeNums) {
		ArrayList<LayerGene> layGenes = new ArrayList<>();
		while (layNodeNums.size() < 2 * layers) {
			int layNum = RNG.getIntRange(1, layers * 4);
			if (!layNodeNums.containsKey(layNum)) {
				layGenes.add(new LayerGene(layNodeNums.size() % 2 != 0, layNum));
				layGenes.add(new LayerGene(layNodeNums.size() % 2 != 0, layNum));
				layNodeNums.put(layNum, new TreeSet<>());
			}
		}
		for (int i = 0; i < layers;) {
			int layNum = RNG.getIntRange(1, layers * 6);
			if (!layNodeNums.containsKey(layNum)) {
				layGenes.add(new LayerGene(false, layNum));
				layGenes.add(new LayerGene(false, layNum));
				i++;
			}
		}
		return layGenes;
	};

	/* Method used at the beginning of the simulation to generate a list of
	 * genes. */
	public static ArrayList<NodeGene> generateNodeGenes(Map<Integer, Set<Integer>> layNodeNums) {
		ArrayList<NodeGene> nodeGenes = new ArrayList<>();
		layNodeNums.forEach((layNum, nodeSet) -> {
			while (nodeSet.size() < 2 * nodes) {
				int nodeNum = RNG.getIntRange(0, nodes * 4);
				if (!nodeSet.contains(nodeNum)) {
					nodeGenes.add(new NodeGene(nodeSet.size() % 2 != 0, layNum, nodeNum));
					nodeGenes.add(new NodeGene(nodeSet.size() % 2 != 0, layNum, nodeNum));
					nodeSet.add(nodeNum);
				}
			}
		});
		for (int i = 0; i < 1 * layers * nodes;) {
			int layNum = RNG.sampleSet(layNodeNums.keySet());
			int nodeNum = RNG.getIntRange(0, nodes * 4);
			Set<Integer> nodeSet = layNodeNums.get(layNum);
			if (!nodeSet.contains(nodeNum)) {
				nodeGenes.add(new NodeGene(nodeSet.size() % 2 != 0, layNum, nodeNum));
				nodeGenes.add(new NodeGene(nodeSet.size() % 2 != 0, layNum, nodeNum));
				nodeSet.add(nodeNum);
				i++;
			}
		}
		for (int i = 0; i < 3*layers*nodes;) {
			int layNum = RNG.getIntRange(0, layers*4);
			if (!layNodeNums.containsKey(layNum)) {
				int nodeNum = RNG.getIntRange(0, nodes*4);
				nodeGenes.add(new NodeGene(false, layNum, nodeNum));
				i++;
			}
		}
		for (int node = 0; node < topNodes; node++) {
			nodeGenes.add(new NodeGene(true, -1, node));
			nodeGenes.add(new NodeGene(true, -1, node));
		}
		return nodeGenes;
	}

	/* Method used at the beginning of the simulation to generate a list of
	*genes. */
	public static ArrayList<ConnGene> generateConnGenes(Map<Integer, Set<Integer>> layNodeNums) {
		ArrayList<ConnGene> connGenes = new ArrayList<>();
		Set<ConnTuple> connTuples = new TreeSet<>();
//		Top Nodes
		connGenes.addAll(generateEndConns(layNodeNums, connTuples, true));
//		Bottom Nodes
		connGenes.addAll(generateEndConns(layNodeNums, connTuples, false));
		Set<Integer> topNodeSet = new TreeSet<>();
		for (int i = 0; i < topNodes; i++) {
			topNodeSet.add(i);
		}
		layNodeNums.put(-1, topNodeSet);
//		Mid Nodes
		layNodeNums.forEach((lay1, nodeSet) -> {
			nodeSet.forEach(node1 -> {
				for (int i = 0; i < conns;) {
					if (generateMidConn(layNodeNums, connGenes, connTuples, lay1, node1, i))
						i++;
				}
			});
		});
		for (int i = 0; i < 3*(layers*nodes*conns + topNodes);) {
			int lay1 = RNG.sampleSet(layNodeNums.keySet());
			int node1 = RNG.sampleSet(layNodeNums.get(lay1));
			if (generateMidConn(layNodeNums, connGenes, connTuples, lay1, node1, i))
				i++;
		}
		return connGenes;
	}

	private static boolean generateMidConn(Map<Integer, Set<Integer>> layNodeNums, List<ConnGene> genes,
			Set<ConnTuple> tuples, int lay1, int node1, int i) {
		int lay2 = RNG.sampleSet(layNodeNums.keySet());
		while (lay1 == lay2) {
			lay2 = RNG.sampleSet(layNodeNums.keySet());
		}
		int node2 = RNG.sampleSet(layNodeNums.get(lay2));
		ConnTuple tuple = null;
		if (comparator.compare(lay1, lay2) > 0) {
			tuple = new ConnTuple(lay2, node2, lay1, node1);
		} else {
			tuple = new ConnTuple(lay1, node1, lay2, node2);
		}
//		System.out.println(tuples.size());
//		System.out.println(layNodeNums.size());
		if (!tuples.contains(tuple)) {
			genes.add(new ConnGene(i % 3 != 0, tuple));
			genes.add(new ConnGene(i % 3 != 0, tuple));
			tuples.add(tuple);
			return true;
		}
		return false;
	}

	private static ArrayList<ConnGene> generateEndConns(Map<Integer, Set<Integer>> layNodeNums, Set<ConnTuple> tuples, boolean top) {
		ArrayList<ConnGene> connGenes = new ArrayList<>();
		int redundancy = (top) ? topRedundancy : bottomRedundancy;
		int nodeCount = (top) ? topNodes : bottomNodes;
		for (int endNode = 0; endNode < nodeCount; endNode++) {
			for (int i = 0; i < 1.5*redundancy;) {
				int layNum = RNG.sampleSet(layNodeNums.keySet());
				int nodeNum = RNG.sampleSet(layNodeNums.get(layNum));
				ConnTuple tuple = null;
				if (top)
					tuple = new ConnTuple(layNum, nodeNum, -1, endNode);
				else
					tuple = new ConnTuple(0, endNode, layNum, nodeNum);
				if (!tuples.contains(tuple)) {
					connGenes.add(new ConnGene(i % 3 != 0, tuple));
					connGenes.add(new ConnGene(i % 3 != 0, tuple));
					tuples.add(tuple);
					i++;
				}
			}
		}
		return connGenes;
	}

	public static Chromosome[] fillChromosomes(List<Gene> genes) {
		Chromosome[] chroms = Chromosome.generate(haploidNum);
		genes.forEach(gene -> chroms[RNG.getIntMax(haploidNum)].append(gene));
		return chroms;
	}

	public static Map<Integer, List<HomologPair>> generateHomologs(Chromosome[] chromosomes) {
		Map<Integer, List<Chromosome>> chromSets = new Hashtable<>();
		for (Chromosome chrom : chromosomes) {
			List<Chromosome> sisters = new ArrayList<>();
			sisters.add(chrom);
			for (int i = 0; i < 5; i++) {
				chrom = chrom.copyAndMutate();
				sisters.add(chrom);
			}
			chromSets.put(chrom.chromNum(), sisters);
		}
		Function<List<Chromosome>, List<HomologPair>> pairMaker = (chroms) -> {
			List<HomologPair> pairList = new ArrayList<>();
			for (Chromosome chromA : chroms) {
				List<Chromosome> others = chroms.subList(0, chroms.indexOf(chromA));
				for (Chromosome chromB : others) {
					pairList.add(new HomologPair(chromA, chromB));
				}
			}
			return pairList;
		};
		return CMUtils.transformMapValues(chromSets, pairMaker);
	}

	public static List<Organism> getFirstGen(Map<Integer, List<HomologPair>> homologPairs) {
		List<Organism> firstGen = new ArrayList<>();
		while (firstGen.size() < populationSize) {
			List<HomologPair> protoGenome = new ArrayList<>();
			homologPairs.forEach((chromNum, pairList) -> {
				protoGenome.add(pairList.get(RNG.getIntMax(pairList.size())));
			});
			firstGen.add(new Organism(new Genome(protoGenome)));
		}
		return firstGen;
	}

}
