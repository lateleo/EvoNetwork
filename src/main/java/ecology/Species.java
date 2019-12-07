package ecology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import staticUtils.CMUtils;
import staticUtils.ComparisonUtils;
import staticUtils.RNG;
import utils.ConnTuple;
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
	public static int topNodes;

	public static double mutationRate;
	public static double mutationMagnitude;
	public static double slipFactor;

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

	}

	public static Population createPopulation(double mRate, double mMag, double slip, int haploidNum,
			int layers, int nodes, int conns, int topRedundancy, int bottomRedundancy, int popSize, int simGens) {

		Species.mutationRate = mRate;
		Species.mutationMagnitude = mMag;
		Species.slipFactor = slip;
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
		Map<Integer, Set<NodeVector>> layNodeNums = new TreeMap<>(ComparisonUtils::compareLayNums);

		genes.addAll(generateLayerGenes(layNodeNums));

		genes.addAll(generateNodeGenes(layNodeNums));
		
		genes.addAll(generateConnGenes(layNodeNums));

		Collections.shuffle(genes);
		return genes;
	}

	/* Method used at the beginning of the simulation to generate a list of
	 * genes. */
	public static ArrayList<LayerGene> generateLayerGenes(Map<Integer, Set<NodeVector>> layNodeNums) {
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
	public static ArrayList<NodeGene> generateNodeGenes(Map<Integer, Set<NodeVector>> layNodeNums) {
		ArrayList<NodeGene> nodeGenes = new ArrayList<>();
		for (Map.Entry<Integer,Set<NodeVector>> entry : layNodeNums.entrySet()) {
			int layNum = entry.getKey();
			Set<NodeVector> nodeSet = entry.getValue();
			while (nodeSet.size() < 2 * nodes) {
				int x = RNG.getIntRange(-nodes, nodes);
				int y = RNG.getIntRange(-nodes, nodes);
				NodeVector vector = new NodeVector(x,y);
				if (!nodeSet.contains(vector)) {
					nodeGenes.add(new NodeGene(nodeSet.size() % 2 != 0, layNum, vector));
					nodeGenes.add(new NodeGene(nodeSet.size() % 2 != 0, layNum, vector));
					nodeSet.add(vector);
				}
			}
		}
		for (int i = 0; i < 1 * layers * nodes;) {
			int layNum = RNG.sampleSet(layNodeNums.keySet());
			int x = RNG.getIntRange(-nodes, nodes);
			int y = RNG.getIntRange(-nodes, nodes);
			NodeVector vector = new NodeVector(x,y);
			Set<NodeVector> nodeSet = layNodeNums.get(layNum);
			if (!nodeSet.contains(vector)) {
				nodeGenes.add(new NodeGene(nodeSet.size() % 2 != 0, layNum, vector));
				nodeGenes.add(new NodeGene(nodeSet.size() % 2 != 0, layNum, vector));
				nodeSet.add(vector);
				i++;
			}
		}
		for (int i = 0; i < 3*layers*nodes;) {
			int layNum = RNG.getIntRange(0, layers*4);
			if (!layNodeNums.containsKey(layNum)) {
				int x = RNG.getIntRange(-nodes, nodes);
				int y = RNG.getIntRange(-nodes, nodes);
				NodeVector vector = new NodeVector(x,y);
				nodeGenes.add(new NodeGene(false, layNum, vector));
				i++;
			}
		}
		for (NodeVector unitVector : NodeVector.unitVectors) {
			double offsetTheta = 2*Math.PI/topNodes;
			NodeVector vector = unitVector.clone().addTheta(offsetTheta).addMagnitude(topNodes);
			nodeGenes.add(new NodeGene(true, -1, vector));
			nodeGenes.add(new NodeGene(true, -1, vector));
		}
		return nodeGenes;
	}

	/* Method used at the beginning of the simulation to generate a list of
	*genes. */
	public static ArrayList<ConnGene> generateConnGenes(Map<Integer, Set<NodeVector>> layNodeNums) {
		ArrayList<ConnGene> connGenes = new ArrayList<>();
		Set<ConnTuple> connTuples = new TreeSet<>();
		connGenes.addAll(generateTopConns(layNodeNums, connTuples));
		connGenes.addAll(generateBottomConns(layNodeNums, connTuples));
		Set<NodeVector> topNodeSet = new TreeSet<>();
		for (NodeVector vector : NodeVector.unitVectors) topNodeSet.add(vector);
		layNodeNums.put(-1, topNodeSet);
//		Mid Nodes
		for (Map.Entry<Integer,Set<NodeVector>> entry : layNodeNums.entrySet()) {
			int lay1 = entry.getKey();
			Set<NodeVector> nodeSet = entry.getValue();
			for (NodeVector vector1 : nodeSet) {
				for (int i = 0; i < conns;) {
					if (generateMidConn(layNodeNums, connGenes, connTuples, lay1, vector1, i)) i++;
				}
			}
		}
		for (int i = 0; i < 3*(layers*nodes*conns + topNodes);) {
			int lay1 = RNG.sampleSet(layNodeNums.keySet());
			NodeVector vector1 = RNG.sampleSet(layNodeNums.get(lay1));
			if (generateMidConn(layNodeNums, connGenes, connTuples, lay1, vector1, i)) i++;
		}
		return connGenes;
	}

	private static boolean generateMidConn(Map<Integer,Set<NodeVector>> layNodeNums, List<ConnGene> genes,
			Set<ConnTuple> tuples, int lay1, NodeVector vector1, int i) {
		int lay2 = RNG.sampleSet(layNodeNums.keySet());
		while (lay1 == lay2) lay2 = RNG.sampleSet(layNodeNums.keySet());
		NodeVector vector2 = RNG.sampleSet(layNodeNums.get(lay2));
		ConnTuple tuple = null;
		if (ComparisonUtils.compareLayNums(lay1, lay2) > 0) tuple = new ConnTuple(lay2, vector2, lay1, vector1);
		else tuple = new ConnTuple(lay1, vector1, lay2, vector2);
		if (!tuples.contains(tuple)) {
			genes.add(new ConnGene(i % 3 != 0, tuple));
			genes.add(new ConnGene(i % 3 != 0, tuple));
			tuples.add(tuple);
			return true;
		}
		return false;
	}

	private static ArrayList<ConnGene> generateTopConns(Map<Integer, Set<NodeVector>> layNodeNums, Set<ConnTuple> tuples) {
		ArrayList<ConnGene> connGenes = new ArrayList<>();
		for (NodeVector topVector : NodeVector.unitVectors) {
			for (int i = 0; i < 1.5*topRedundancy;) {
				int layNum = RNG.sampleSet(layNodeNums.keySet());
				NodeVector randVector = RNG.sampleSet(layNodeNums.get(layNum));
				ConnTuple tuple =  new ConnTuple(layNum, randVector, -1, topVector);
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
	
	private static ArrayList<ConnGene> generateBottomConns(Map<Integer,Set<NodeVector>> layNodeNums, Set<ConnTuple> tuples) {
		ArrayList<ConnGene> connGenes = new ArrayList<>();
		int start = -bottomWidth/2;
		int stop = bottomWidth + start;
		for (NodeVector bottomVector : NodeVector.bottomVectors) {
			for (int i = 0; i < 1.5*bottomRedundancy;) {
				int layNum = RNG.sampleSet(layNodeNums.keySet());
				NodeVector randVector = RNG.sampleSet(layNodeNums.get(layNum));
				ConnTuple tuple = new ConnTuple(0, bottomVector, layNum, randVector);
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
