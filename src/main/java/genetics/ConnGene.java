package genetics;

import ecology.Species;
import staticUtils.RNG;
import utils.ConnTuple;

/*
 * This class represents genes that determine the presence/absence and weight of connections between nodes in a network.
 * 'xprLevel' is a value that roughly represents how strongly a gene is expressed,
 * 'inLayNum' and 'inNodeNum' indicate the layer and node numbers, respectively, of the node whose output is to be used,
 * 'outLayNum' and 'outNodeNum' indicate the layer and node numbers, respectively, of the node taking in said output,
 * 'weight' indicates the connection's weight value.
 */
public class ConnGene extends Gene {
	private static double mMag = Species.mutationMagnitude;
	public double xprLevel, inLayNum, inNodeNum, outLayNum, outNodeNum, weight;
//	int signature;
	
	Mutation xprMutation = (gene) -> ((ConnGene) gene).xprLevel += RNG.getShiftDouble()*mMag;
	Mutation layNumMutation = (gene) -> {
		ConnGene mutant = (ConnGene) gene;
		if (RNG.getBoolean()) mutant.inLayNum = Math.max(0.0, mutant.inLayNum + RNG.getShiftDouble()*mMag);
		else mutant.outLayNum = Math.max(-1.0, mutant.outLayNum + RNG.getShiftDouble()*mMag);
	};
	Mutation nodeNumMutation = (gene) -> {
		ConnGene mutant = (ConnGene) gene;
		if (RNG.getBoolean()) mutant.inNodeNum = Math.max(0.0, mutant.inNodeNum + RNG.getShiftDouble()*mMag);
		else mutant.outNodeNum = Math.max(0.0, mutant.outNodeNum + RNG.getShiftDouble()*mMag);		
	};
	Mutation weightMutation = (gene) -> ((ConnGene) gene).weight *= 1 + RNG.getGauss()*mMag;
	
	public ConnGene(double xprLevel, double inLayNum, double inNodeNum,
			double outLayNum, double outNodeNum, double weight) {
		this.xprLevel = xprLevel;
		this.inLayNum = inLayNum;
		this.inNodeNum = inNodeNum;
		this.outLayNum = outLayNum;
		this.outNodeNum = outNodeNum;
		this.weight = weight;
	}
	
	public ConnGene(boolean positive, ConnTuple tuple) {
		this.xprLevel = ((positive) ? 1 : -1)*RNG.getHalfGauss();
		this.inLayNum = tuple.iLay() + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.inNodeNum = tuple.iNode() + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.outLayNum = tuple.oLay() + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.outNodeNum = tuple.oNode() + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.weight = RNG.getGauss();
	}
	


	@Override
	protected Gene clone() {
		return new ConnGene(this.xprLevel, this.inLayNum, this.inNodeNum, 
				this.outLayNum, this.outNodeNum, this.weight);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(new Mutation[] {xprMutation, layNumMutation, nodeNumMutation, weightMutation}, rand);
	}

}
