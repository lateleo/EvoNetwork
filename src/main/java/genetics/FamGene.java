package genetics;

import java.util.Arrays;
import utils.RNG;

public class FamGene extends Gene {
	double xprLevel, weight;
	long signFilter;
	
	Mutation xprMutation = (gene) -> ((FamGene) gene).xprLevel += RNG.getShiftDouble();
	Mutation weightMutation = (gene) -> ((FamGene) gene).weight += RNG.getShiftDouble();
	Mutation filterMutation = (gene) -> {
		FamGene mutant = (FamGene) gene;
		mutant.signFilter = mutant.signFilter ^ (long) Math.pow(2, RNG.getLongBit());
	};
	
	
	public FamGene(double xprLevel, double weight, long signFilter) {
		this.xprLevel = xprLevel;
		this.weight = weight;
		this.signFilter = signFilter;
	}

	@Override
	protected Gene clone() {
		return new FamGene(this.xprLevel, this.weight, this.signFilter);
	}

	@Override
	public Gene mutate() {
		return mutate(Arrays.asList(xprMutation, weightMutation, filterMutation));
	}

}
