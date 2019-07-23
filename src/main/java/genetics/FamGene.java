package genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ecology.Species;
import utils.RNG;

public class FamGene extends Gene {
	private static double mMag = Species.mutationMagnitude;
	double xprLevel, weight;
	int signFilter;
	
	Mutation xprMutation = (gene) -> ((FamGene) gene).xprLevel += RNG.getShiftDouble()*mMag;
	Mutation weightMutation = (gene) -> ((FamGene) gene).weight += RNG.getGauss()*mMag;
	Mutation filterMutation = (gene) -> {
		FamGene mutant = (FamGene) gene;
		mutant.signFilter = mutant.signFilter ^ (int) Math.pow(2, RNG.getBit());
	};
	
	
	public FamGene(double xprLevel, double weight, int signFilter) {
		this.xprLevel = xprLevel;
		this.weight = weight;
		this.signFilter = signFilter;
	}
	
	public static ArrayList<Gene> generate(int fams, int diploidNum, int signBits) {
		ArrayList<Gene> genes = new ArrayList<>();
		int geneNum = fams;
		double xprShift = 1;
		while (genes.size() < geneNum) {
			int[] bits = new int[signBits];
			for (int i = 0; i < bits.length; i++) bits[i] = RNG.getBit();
			int sign = Arrays.stream(bits).sum();
			genes.add(new FamGene(RNG.getGauss() + xprShift, RNG.getGauss(), sign));
		}
		return genes;
	}

	@Override
	protected Gene clone() {
		return new FamGene(this.xprLevel, this.weight, this.signFilter);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(new Mutation[] {xprMutation, weightMutation, filterMutation}, rand);
	}

}
