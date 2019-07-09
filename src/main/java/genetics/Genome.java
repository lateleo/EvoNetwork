package genetics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ecology.Species;
import utils.RNG;

public class Genome extends ArrayList<Chromosome> {
	private static final long serialVersionUID = -5681670623380224763L;  //This is just because ArrayLists are Serializable
	private static double slipChance = Species.slipChance;
	
	public Genome(List<Chromosome> chromosomes) {
		this.addAll(chromosomes);
	}
	
	public Genome copy() {
		List<Chromosome> copies = new ArrayList<Chromosome>();
		for (Chromosome chrom : this) copies.add(chrom.copy());
		return new Genome(copies);
	}
	
	public Chromosome getRecombinant(Chromosome chromA, Chromosome chromB) {
		int minHead = Math.min(chromA.getHead(), chromB.getHead());
		int minTail = Math.min(chromA.getTail(), chromB.getTail());
		int randIndex = RNG.getIntRange(0 - minHead, minTail);
		boolean whichChrom = RNG.getBoolean();
		Chromosome newChrom = ((whichChrom)?chromA:chromB).copy();
		int offsetA = randIndex, offsetB = randIndex;
		int slip;
		if (randIndex < 0) {
			offsetA += (chromA.getHead()>minHead)? 0 - RNG.getIntMax(chromA.getHead()-minHead) : 0;
			offsetB += (chromB.getHead()>minHead)? 0 - RNG.getIntMax(chromB.getHead()-minHead) : 0;
			slip = (int) RNG.getBoundGauss(0-newChrom.getHead()-randIndex, 0-randIndex, slipChance);
		} else {
			offsetA += (chromA.getTail()>minTail)? RNG.getIntMax(chromA.getTail()-minTail) : 0;
			offsetB += (chromB.getTail()>minTail)? RNG.getIntMax(chromB.getTail()-minTail) : 0;
			slip = (int) RNG.getBoundGauss(0-randIndex, newChrom.getTail()-randIndex, slipChance);
		}
		List<Gene> subSequence = (whichChrom)? chromB.subSequence(offsetB) : chromA.subSequence(offsetA);
		newChrom.recombine(((whichChrom)?offsetA:offsetB) + slip, subSequence);
		return newChrom;
	}
	
	public List<Chromosome> getHaploidSet() {
		Comparator<Chromosome> chromSorter = (a,b) -> a.chromNum() - b.chromNum();
		List<Chromosome> diploidSet = new ArrayList<Chromosome>(this);
		List<Chromosome> haploidSet = new ArrayList<Chromosome>();
		diploidSet.sort(chromSorter);
		while (!diploidSet.isEmpty()) {
			Chromosome chromA = diploidSet.remove(0);
			Chromosome chromB = diploidSet.remove(0);
			haploidSet.add(getRecombinant(chromA, chromB).mutateAll());
		}
		return haploidSet;
	}
	
	public Transcriptome trasncribe() {
		return new Transcriptome(this);
	}

}
