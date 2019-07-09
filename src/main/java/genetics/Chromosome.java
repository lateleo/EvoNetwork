package genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class Chromosome extends ArrayList<Gene> {
	private static final long serialVersionUID = 2471841169414882405L; //This is just because ArrayLists are Serializable
	
	private Centromere centromere;
	
	public Chromosome(int chromosomeNum) {
		this.centromere = new Centromere(chromosomeNum);
		this.add(this.centromere);
	}
	
	private Chromosome(Chromosome original) {
		this.centromere = original.centromere;
		this.addAll(original);
	}
	
	public Chromosome copy() {
		return new Chromosome(this);
	}
	
	int getHead() {
		return this.indexOf(this.centromere);
	}
	
	int getTail() {
		return this.size() - this.indexOf(this.centromere);
	}
	
	int chromNum() {
		return this.centromere.getChromosomeNum();
	}
	
	Chromosome mutateAll() {
		UnaryOperator<Gene> mutator = (gene) -> gene.mutate();
		this.replaceAll(mutator);
		return this;
	}
	
	void recombine(int randOffset, List<Gene> subSequence) {
		if (randOffset < 0) {
			this.removeRange(0, getHead() + randOffset);
			this.addAll(0, subSequence);
		} else {
			this.removeRange(getHead() + randOffset, this.size());
			this.addAll(subSequence);
		}
	}
	
	List<Gene> subSequence(int randOffset) {
		if (randOffset < 0) {
			return this.subList(0, randOffset + getHead());
		} else {
			return this.subList(randOffset + getHead(), this.size());
		}
	}
	
	List<Gene> getGenes() {
		List<Gene> genes = new ArrayList<Gene>(this);
		genes.remove(this.centromere);
		return genes;
	}
	
	


}
