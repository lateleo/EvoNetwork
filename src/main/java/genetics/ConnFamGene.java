package genetics;

import java.util.Arrays;
import utils.RNG;

public class ConnFamGene extends Gene {
	double xprLevel, weight;
	long signFilter;
	
	Mutation xprMutation = (gene) -> ((ConnFamGene) gene).xprLevel += RNG.getShiftDouble();
	Mutation weightMutation = (gene) -> ((ConnFamGene) gene).weight += RNG.getShiftDouble();
	Mutation filterMutation = (gene) -> {
		ConnFamGene mutant = (ConnFamGene) gene;
		mutant.signFilter = mutant.signFilter ^ (long) Math.pow(2, RNG.getLongBits());
	};
	
	
	public ConnFamGene(double xprLevel, double weight, long signFilter) {
		this.xprLevel = xprLevel;
		this.weight = weight;
		this.signFilter = signFilter;
	}

	@Override
	protected Gene clone() {
		return new ConnFamGene(this.xprLevel, this.weight, this.signFilter);
	}

	@Override
	public Gene mutate() {
		return mutate(Arrays.asList(xprMutation, weightMutation, filterMutation));
	}

}
