package genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import utils.RNG;


public class Chromosome extends ArrayList<Gene> {
	private static final long serialVersionUID = 2471841169414882405L; //This is just because ArrayLists are Serializable
	private Centromere centromere;
	
	public Chromosome(int chromosomeNum) {
		centromere = new Centromere(chromosomeNum);
		add(centromere);
	}
	
	private Chromosome(Chromosome original) {
		centromere = original.centromere;
		addAll(original);
	}
	
	public Chromosome copy() {
		return new Chromosome(this);
	}
	
	public Chromosome copyAndMutate() {
		return new Chromosome(this).mutateAll().mutateAll().mutateAll().mutateAll();
	}
	
	public static Chromosome[] generate(int diploidNum) {
		Chromosome[] chromosomes = new Chromosome[diploidNum];
		for (int i = 0; i < diploidNum; i++) {
			chromosomes[i] = new Chromosome(i+1);
		}
		return chromosomes;
	}
	
	int getHead() {
		return indexOf(centromere);
	}
	
	int getTail() {
		return size() - indexOf(centromere);
	}
	
	public int chromNum() {
		return centromere.getChromosomeNum();
	}
	
	Chromosome mutateAll() {
		UnaryOperator<Gene> mutator = (gene) -> gene.mutate();
		replaceAll(mutator);
		return this;
	}
	
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
		}

	}
	
	List<Gene> subSequence(int randOffset) {
		if (randOffset < 0) {
			return subList(0, randOffset + getHead());
		} else {
			return subList(randOffset + getHead(), size());
		}
	}
	
	List<Gene> getGenes() {
		List<Gene> genes = new ArrayList<Gene>(this);
		genes.remove(centromere);
		return genes;
	}

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
