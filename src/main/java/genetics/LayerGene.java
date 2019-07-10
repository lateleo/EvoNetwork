package genetics;

import java.util.Arrays;
import utils.RNG;

public class LayerGene extends Gene {
	double xprLevel, layerNum;
	Mutation xprMutation = (mutant) -> ((LayerGene) mutant).xprLevel += RNG.getShiftDouble();
	Mutation layNumMutation = (gene) -> {
		LayerGene mutant = (LayerGene) gene;
		mutant.layerNum = Math.max(1.0, mutant.layerNum + RNG.getShiftDouble());
	};
	
	
	public LayerGene(double xprLevel, double layerNum) {
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
	}

	@Override
	protected Gene clone() {
		return new LayerGene(this.xprLevel, this.layerNum);
	}

	@Override
	public Gene mutate() {
		return mutate(Arrays.asList(xprMutation, layNumMutation));
	}

}
