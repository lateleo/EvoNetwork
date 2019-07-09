package genetics;

import java.util.Arrays;
import utils.RNG;

public class ConnGene extends Gene {
	public double xprLevel, inLayNum, inNodeNum, outLayNum, outNodeNum, weight;
	long signature;
	
	Mutation xprMutation = (gene) -> ((ConnGene) gene).xprLevel += RNG.getShiftDouble();
	Mutation layNumMutation = (gene) -> {
		ConnGene mutant = (ConnGene) gene;
		if (RNG.getBoolean()) mutant.inLayNum = Math.max(0.0, mutant.inLayNum + RNG.getShiftDouble());
		else mutant.outLayNum = Math.max(-1.0, mutant.outLayNum + RNG.getShiftDouble());
	};
	Mutation nodeNumMutation = (gene) -> {
		ConnGene mutant = (ConnGene) gene;
		if (RNG.getBoolean()) mutant.inNodeNum = Math.max(0.0, mutant.inNodeNum + RNG.getShiftDouble());
		else mutant.outNodeNum = Math.max(0.0, mutant.outNodeNum + RNG.getShiftDouble());		
	};
	Mutation weightMutation = (gene) -> ((ConnGene) gene).weight += RNG.getShiftDouble();
	Mutation signMutation = (gene) -> {
		ConnGene mutant = (ConnGene) gene;
		mutant.signature = mutant.signature ^ (long) Math.pow(2, RNG.getLongBit());
	};
	
	public ConnGene(double xprLevel, double inLayNum, double inNodeNum,
			double outLayNum, double outNodeNum, double weight, long signature) {
		this.xprLevel = xprLevel;
		this.inLayNum = inLayNum;
		this.inNodeNum = inNodeNum;
		this.outLayNum = outLayNum;
		this.outNodeNum = outNodeNum;
		this.weight = weight;
		this.signature = signature;
	}

	@Override
	protected Gene clone() {
		return new ConnGene(this.xprLevel, this.inLayNum, this.inNodeNum, 
				this.outLayNum, this.outNodeNum, this.weight, this.signature);
	}

	@Override
	public Gene mutate() {
		return mutate(Arrays.asList(xprMutation, layNumMutation, nodeNumMutation, weightMutation, signMutation));
	}

}
