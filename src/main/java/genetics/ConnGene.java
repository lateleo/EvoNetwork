package genetics;

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
	private static Mutation[] mutations = new Mutation[] {
			(gene) -> gene.mutateXpr(),
			(gene) -> ((ConnGene) gene).mutateInput(),
			(gene) -> ((ConnGene) gene).mutateOutput(),
			(gene) -> ((ConnGene) gene).mutateWeight()
	};
	
	public double inLayNum, inNodeNum, outLayNum, outNodeNum, weight;
	
	
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
	
	private void mutateInput() {
		if (RNG.getBoolean()) inLayNum = Math.max(0.0, inLayNum + RNG.getShiftDouble(mMag));
		else inNodeNum = Math.max(0.0, inNodeNum + RNG.getShiftDouble(mMag));
	}
	
	private void mutateOutput() {
		if (RNG.getBoolean()) outLayNum = Math.max(0.0, outLayNum + RNG.getShiftDouble(mMag));
		else outNodeNum = Math.max(0.0, outNodeNum + RNG.getShiftDouble(mMag));
	}
	
	private void mutateWeight() {
		weight *= RNG.getGauss(1, mMag);
	}

	@Override
	protected Gene clone() {
		return new ConnGene(this.xprLevel, this.inLayNum, this.inNodeNum, 
				this.outLayNum, this.outNodeNum, this.weight);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(mutations, rand);
	}

}
