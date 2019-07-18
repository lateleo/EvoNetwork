package genetics;

import java.util.ArrayList;
import java.util.List;

import ecology.Species;
import utils.RNG;

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
	
	public Chromosome getRecombinant() {
		int minHead = Math.min(a.getHead(), b.getHead());
		int minTail = Math.min(a.getTail(), b.getTail());
		int randIndex = RNG.getIntRange(0 - minHead, minTail);
		boolean whichChrom = RNG.getBoolean();
		Chromosome newChrom = ((whichChrom)?a:b).copy();
		int offsetA = randIndex, offsetB = randIndex;
		int slip;
		if (randIndex < 0) {
			offsetA += (a.getHead()>minHead)? 0 - RNG.getIntMax(1+a.getHead()-minHead) : 0;
			offsetB += (b.getHead()>minHead)? 0 - RNG.getIntMax(1+b.getHead()-minHead) : 0;
			slip = (int) RNG.getBoundGauss(0-newChrom.getHead()-randIndex, 0-randIndex, slipFactor);
		} else {
			offsetA += (a.getTail()>minTail)? RNG.getIntMax(1+a.getTail()-minTail) : 0;
			offsetB += (b.getTail()>minTail)? RNG.getIntMax(1+b.getTail()-minTail) : 0;
			slip = (int) RNG.getBoundGauss(0-randIndex, newChrom.getTail()-randIndex, slipFactor);
		}
		List<Gene> subSequence = (whichChrom)? b.subSequence(offsetB) : a.subSequence(offsetA);
		newChrom.recombine(((whichChrom)?offsetA:offsetB) + slip, subSequence);
		newChrom = newChrom.mutateAll();
		return newChrom;
	}
	
	List<Gene> getGenes() {
		List<Gene> genes = new ArrayList<Gene>(a);
		genes.addAll(b.getGenes());
		return genes;
	}
	
	public int size() {
		return a.size() + b.size() - 2;
	}
}
