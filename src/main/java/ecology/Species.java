package ecology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import data.MnistDataReader;
import data.MnistImage;
import genetics.Chromosome;
import genetics.ConnGene;
import genetics.FamGene;
import genetics.Gene;
import genetics.Genome;
import genetics.HomologPair;
import genetics.LayerGene;
import genetics.NodeGene;
import network.Organism;
import utils.CMUtils;
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

	public static int populationSize;
	public static int simulatedGenerations;

	public static void initialize(String dataset, int batches) {
		mnistReader = new MnistDataReader(dataset);
		try {
			testImages = mnistReader.readTestingData();
			MnistImage[] allImages = mnistReader.readTrainingData();
			int imgCount = allImages.length;
			int batchSize = imgCount/batches;
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

	public static Population createPopulation(double mRate, double mMag, double slip, int diploidNum, int layers, int nodes,
			int conns, int signBits, int fams, int popSize, int simGens) {

		mutationRate = mRate;
		mutationMagnitude = mMag;
		slipFactor = slip;
		populationSize = popSize;
		simulatedGenerations = simGens;

		System.out.println("Generating Genes...");
		List<Gene> genes = generateGenes(diploidNum, layers, nodes, conns, signBits, fams);
		System.out.println("Filling Chromosomes...");
		Chromosome[] chromosomes = fillChromosomes(genes, diploidNum);
		System.out.println("Generating Homologs...");
		Map<Integer, List<HomologPair>> homologPairs = generateHomologs(chromosomes);
		System.out.println("Creating First Generation...");
		List<Organism> orgs = getFirstGen(homologPairs, popSize);
		return new Population(orgs);
	}

	public static List<Gene> generateGenes(int diploidNum, int layers, int nodes, int conns, int signBits, int fams) {
		ArrayList<Gene> genes = LayerGene.generate(layers, diploidNum);
		genes.addAll(NodeGene.generate(layers, nodes, diploidNum));
		genes.addAll(ConnGene.generate(layers, nodes, conns, diploidNum, signBits));
		genes.addAll(FamGene.generate(fams, diploidNum, signBits));
		Collections.shuffle(genes);
		return genes;
	}

	public static Chromosome[] fillChromosomes(List<Gene> genes, int diploidNum) {
		Chromosome[] chroms = Chromosome.generate(diploidNum);
		genes.forEach(gene -> chroms[RNG.getIntMax(diploidNum)].append(gene));
		return chroms;
	}

	public static Map<Integer, List<HomologPair>> generateHomologs(Chromosome[] chromosomes) {
		Map<Integer, List<Chromosome>> chromSets = new Hashtable<>();
		for (Chromosome chrom : chromosomes) {
			List<Chromosome> sisters = new ArrayList<>();
			sisters.add(chrom);
			for (int i = 0; i < 5; i++)
				chrom = chrom.copyAndMutate();
				sisters.add(chrom);
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
