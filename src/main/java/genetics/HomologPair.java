package genetics;

import java.util.ArrayList;
import java.util.List;

import ecology.Species;
import utils.RNG;

/*
 * represents a homologous pair of chromosomes (chromosomes with the same chromosome number, each one coming from a different parent).
 * used in the organization of the genome.
 */
public class HomologPair {
	private static double slipFactor = Species.slipFactor;
	int chromNum;
	Chromosome a;
	Chromosome b;
	
	public HomologPair(Chromosome a, Chromosome b) {
		this.a = a;
		this.b = b;
		this.chromNum = a.chromNum();

	}
	
	public HomologPair copy() {
		return new HomologPair(this.a.copy(), this.b.copy());
	}
	
	/*
	 * Used in the reproduction process to get a single recombinant chromosome from both homologs.
	 */
	public Chromosome getRecombinant(boolean forcedMutation) {
		int minHead = Math.min(a.getHead(), b.getHead());
		int minTail = Math.min(a.getTail(), b.getTail());
		int randIndex = RNG.getIntRange(0 - minHead, minTail);
		boolean whichChrom = RNG.getBoolean();
		Chromosome newChrom = ((whichChrom)?a:b).copy();
		int slip;
		if (randIndex < 0) {
			slip = (int) RNG.getBoundGauss(0-(newChrom.getHead() + randIndex), 0-randIndex, slipFactor);
		} else {
			slip = (int) RNG.getBoundGauss(0-randIndex, newChrom.getTail()-randIndex, slipFactor);
		}
		List<Gene> subSequence = ((whichChrom)? b : a).subSequence(randIndex);
		newChrom.recombine(randIndex + slip, subSequence);
		return newChrom.mutateAll(forcedMutation);
	}
	
	/*
	 * Used in transcription to get a list of all genes (except for centromeres) from both chromosomes.
	 */
	List<Gene> getGenes() {
		List<Gene> genes = new ArrayList<Gene>(a);
		genes.addAll(b.getGenes());
		return genes;
	}
	
	public int size() {
		return a.size() + b.size() - 2;
	}
}
