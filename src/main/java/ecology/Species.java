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

	public static int populationSize;
	public static int simulatedGenerations;
	
	public static Comparator<Integer> comparator = (int1, int2) -> {
		if (int1 == int2) return 0;
		if (int1 == -1) return 1;
		if (int2 == -1) return -1;
		else return int1 - int2;
	};

	public static void initialize(String dataset, int batchSize) {
		mnistReader = new MnistDataReader(dataset);
		try {
			testImages = mnistReader.readTestingData();
			MnistImage[] allImages = mnistReader.readTrainingData();
			int imgCount = allImages.length;
			int batches = imgCount/batchSize;
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

	public static Population createPopulation(double mRate, double mMag, double slip, double lRate, int haploidNum, int layers, int nodes,
			int conns, int popSize, int simGens) {

		mutationRate = mRate;
		mutationMagnitude = mMag;
		slipFactor = slip;
		learningRate = lRate;
		populationSize = popSize;
		simulatedGenerations = simGens;

//		System.out.println("Generating Genes...");
		List<Gene> genes = generateGenes(haploidNum, layers, nodes, conns);
//		System.out.println("Filling Chromosomes...");
		Chromosome[] chromosomes = fillChromosomes(genes, haploidNum);
//		System.out.println("Generating Homologs...");
		Map<Integer, List<HomologPair>> homologPairs = generateHomologs(chromosomes);
//		System.out.println("Creating First Generation...");
		List<Organism> orgs = getFirstGen(homologPairs, popSize);
		return new Population(orgs);
	}

	public static List<Gene> generateGenes(int haploidNum, int layers, int nodes, int conns) {
		ArrayList<Gene> genes = new ArrayList<>();
		ArrayList<LayerGene> layGenes = generateLayerGenes(layers);
		genes.addAll(layGenes);
		ArrayList<NodeGene> nodeGenes = generateNodeGenes(layGenes, layers, nodes);
		genes.addAll(nodeGenes);
		ArrayList<ConnGene> connGenes = generateConnGenes(nodeGenes, conns);
		genes.addAll(connGenes);
		Collections.shuffle(genes);
		return genes;
	}
	
	/*
	 *Method used at the beginning of the simulation to generate a list of genes.
	 */
	public static ArrayList<LayerGene> generateLayerGenes(int layers){
		ArrayList<LayerGene> layGenes = new ArrayList<>();
		Set<Integer> layNums = new HashSet<>();
		while (layNums.size() < 2*layers) {
			int layNum = (int) RNG.getMinGauss(1, layers, 1+3*layers);
			if (!layNums.contains(layNum)) {
				layGenes.add(new LayerGene(true, layNum));
				layGenes.add(new LayerGene(false, layNum));
				layGenes.add(new LayerGene(layNums.size() % 2 == 0, layNum));
				layNums.add(layNum);
			}
		}
		return layGenes;
	};
	
	/*
	 *Method used at the beginning of the simulation to generate a list of genes.
	 */
	public static ArrayList<NodeGene> generateNodeGenes(ArrayList<LayerGene> layGenes, int layers, int nodes) {
		ArrayList<NodeGene> nodeGenes = new ArrayList<>();
		Map<Integer,Set<Integer>> nodeNums = new TreeMap<>();
		for (LayerGene layGene : layGenes) {
			int layNum = (int) layGene.layerNum;
			if (!nodeNums.containsKey(layNum)) nodeNums.put(layNum, new HashSet<Integer>());
		}
		ArrayList<Integer> layNums = new ArrayList<>(nodeNums.keySet());
		while (nodeGenes.size() < 4*layers*nodes) {
			int layNum = layNums.get(RNG.getIntMax(layNums.size()));
			int nodeNum = (int) RNG.getMinGauss(0, nodes, 3*nodes);
			Set<Integer> laySet = nodeNums.get(layNum);
			if (!laySet.contains(nodeNum)) {
				nodeGenes.add(new NodeGene(true, layNum, nodeNum));
				nodeGenes.add(new NodeGene(false, layNum, nodeNum));
				nodeGenes.add(new NodeGene(laySet.size() % 2 == 0, layNum, nodeNum));
				laySet.add(nodeNum);
			}
		}
		for (int node = 0; node < topNodes; node++) {
			nodeGenes.add(new NodeGene(true, -1, node));
			nodeGenes.add(new NodeGene(true, -1, node));

		}
		return nodeGenes;		
	}
	
	/*
	 *Method used at the beginning of the simulation to generate a list of genes.
	 */
	public static ArrayList<ConnGene> generateConnGenes(ArrayList<NodeGene> nodeGenes, int conns) {
		int redundancy = 3;
		ArrayList<ConnGene> connGenes = new ArrayList<>();
		Set<ConnTuple> connTuples = new HashSet<>();
//		Top Nodes
		generateEndConns(redundancy, nodeGenes, connGenes, connTuples, true);
//		Bottom Nodes
		generateEndConns(redundancy, nodeGenes, connGenes, connTuples, false);
//		Mid Nodes
		for (int i = 0; i < conns; i++) {
			int ceil = 2*(redundancy*(topNodes+bottomNodes) + i + 1);
			while (connTuples.size() < ceil) {
				NodeGene a = nodeGenes.get(RNG.getIntMax(nodeGenes.size()));
				NodeGene b = nodeGenes.get(RNG.getIntMax(nodeGenes.size()));
				while (Math.floor(a.layerNum) == Math.floor(b.layerNum)) {
					b = nodeGenes.get(RNG.getIntMax(nodeGenes.size()));
				}
				ConnTuple tuple = null;
					if (comparator.compare((int) a.layerNum, (int) b.layerNum) > 0) {
						tuple = new ConnTuple((int) b.layerNum, (int) b.nodeNum, (int) a.layerNum, (int) a.nodeNum);
					} else {
						tuple = new ConnTuple((int) a.layerNum, (int) a.nodeNum, (int) b.layerNum, (int) b.nodeNum);
					}
				if (!connTuples.contains(tuple)) {
					connGenes.add(new ConnGene(true, tuple));
					connGenes.add(new ConnGene(false, tuple));
					connTuples.add(tuple);
				}
			}
		}
		return connGenes;	
	}
	
	private static void generateEndConns(int red, List<NodeGene> nodes, List<ConnGene> conns, Set<ConnTuple> tuples, boolean top) {
		int nodeCount = (top) ? topNodes : bottomNodes;
		for (int node = 0; node < nodeCount; node++) {
			int ceil = 2*red*(node + ((top) ? 1 : 1 + topNodes));
			while (tuples.size() < ceil) {
				NodeGene nodeGene = nodes.get(RNG.getIntMax(nodes.size()));
				ConnTuple tuple = null;
				if (top) tuple = new ConnTuple((int) nodeGene.layerNum, (int) nodeGene.nodeNum, -1, node);
				else tuple = new ConnTuple(0, node, (int) nodeGene.layerNum, (int) nodeGene.nodeNum);
				if (!tuples.contains(tuple)) {
					conns.add(new ConnGene(true, tuple));
					conns.add(new ConnGene(false, tuple));
					tuples.add(tuple);
				}
			}
		}
	}

	public static Chromosome[] fillChromosomes(List<Gene> genes, int haploidNum) {
		Chromosome[] chroms = Chromosome.generate(haploidNum);
		genes.forEach(gene -> chroms[RNG.getIntMax(haploidNum)].append(gene));
		return chroms;
	}

	public static Map<Integer, List<HomologPair>> generateHomologs(Chromosome[] chromosomes) {
		Map<Integer, List<Chromosome>> chromSets = new Hashtable<>();
		for (Chromosome chrom : chromosomes) {
			List<Chromosome> sisters = new ArrayList<>();
			sisters.add(chrom);
			for (int i = 0; i < 1; i++) {
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

	public static List<Organism> getFirstGen(Map<Integer, List<HomologPair>> homologPairs, int size) {
		List<Organism> firstGen = new ArrayList<>();
		while (firstGen.size() < size) {
			List<HomologPair> protoGenome = new ArrayList<>();
			homologPairs.forEach((chromNum, pairList) -> {
				protoGenome.add(pairList.get(RNG.getIntMax(pairList.size())));
			});
			firstGen.add(new Organism(new Genome(protoGenome)));
		}
		return firstGen;
	}

}
