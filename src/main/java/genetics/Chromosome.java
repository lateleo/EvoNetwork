package genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import ecology.Species;
import staticUtils.RNG;

/*
 * An extension of the built-in ArrayList class, explicitly only containing Gene objects, including it's own Centromere,
 * that has additional Gene-specific functionality.
 */
public class Chromosome extends ArrayList<Gene> {
	private static final long serialVersionUID = 2471841169414882405L; //This is just because ArrayLists are Serializable
	private Centromere centromere;
	private static double mutationRate = Species.mutationRate;
	private static double invMutationRate; // used to speed up computation
	
	public Chromosome(int chromosomeNum) {
		centromere = new Centromere(chromosomeNum);
		add(centromere);
	}
	
	/*
	 * Used in the public 'copy' method.
	 */
	private Chromosome(Chromosome original) {
		centromere = original.centromere;
		addAll(original);
	}
	
	public Chromosome copy() {
		return new Chromosome(this);
	}
	
	/*
	 * Used during intial genome creation, to speed up diversification and variety within the genome.
	 */
	public Chromosome copyAndMutate() {
		return new Chromosome(this).mutateAll(true).mutateAll(true);
	}
	
	/*
	 * Used during initial genome creation, to create a full haploid set of initially empty (except for centromeres) chromosomes.
	 */
	public static Chromosome[] generate(int diploidNum) {
		Chromosome[] chromosomes = new Chromosome[diploidNum];
		for (int i = 0; i < diploidNum; i++) {
			chromosomes[i] = new Chromosome(i+1);
		}
		return chromosomes;
	}
	
	/*
	 * Used in recombination to get the number of genes before the centromere.
	 */
	int getHead() {
		return indexOf(centromere);
	}
	
	/*
	 * Used in recombination to get the number of genes after the centromere.
	 */
	int getTail() {
		return size() - indexOf(centromere);
	}
	
	public int chromNum() {
		return centromere.getChromosomeNum();
	}
	
	/*
	 * Used in recombination (and the above 'copyAndMutate' method) to mutate all genes contained in the chromosome.
	 */
	Chromosome mutateAll(boolean forced) {
		UnaryOperator<Gene> mutator;
		if (forced) {
			mutator = (gene) -> gene.mutate(RNG.getDouble());
		} else {
			mutator = (gene) -> {
				double rand = RNG.getDouble();
				if (rand < mutationRate) return gene.mutate(rand*invMutationRate);
				else return gene;
			};
		}
		replaceAll(mutator);
		return this;
	}

	/*
	 * Replaces a particular subsequence of itself, determined by 'randOffset', with the provided 'subSequence'
	 * from it's homolog.
	 */
	void recombine(int randOffset, List<Gene> subSequence) {
		try {
			if (randOffset < 0) {
				removeRange(0, getHead() + randOffset);
				addAll(0, subSequence);
			} else {
				removeRange(getHead() + randOffset, size());
				addAll(subSequence);
			}			
		} catch (Exception e) {
			System.out.println("randOffset: " + randOffset);
			System.out.println("subSequence size: " + subSequence.size());
			System.out.println("getHead(): " + getHead());
			System.out.println("Chromosome size: " + size());
			e.printStackTrace();
		}

	}
	
	/*
	 * Used in recombination to give a subset of itself to it's homolog.
	 */
	List<Gene> subSequence(int randOffset) {
		if (randOffset < 0) {
			return subList(0, randOffset + getHead());
		} else {
			return subList(randOffset + getHead(), size());
		}
	}
	
	/*
	 * Used in Transcription to get a list of all genes in the chromosome, except the centromere.
	 */
	List<Gene> getGenes() {
		List<Gene> genes = new ArrayList<Gene>(this);
		genes.remove(centromere);
		return genes;
	}

	/*
	 * Used during genome creation to randomly add genes to either the beginning or end of the ArrayList.
	 */
	public void append(Gene gene) {
		if (RNG.getBoolean()) add(0,gene);
		else add(gene);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(centromere.getChromosomeNum()).append(": ").append(size()).append("]");
		return builder.toString();
	}


}
