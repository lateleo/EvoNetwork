package genetics;

import java.text.DecimalFormat;

import staticUtils.RNG;

/*
 * This class represents genes that determine the presence/absence of additional layers in a network.
 * 'xprLevel' is a value that roughly represents how strongly a gene is expressed,
 * and 'layerNum' indicates which layer the gene encodes.
 */
public class LayerGene extends Gene {
	private static Mutation[] mutations = new Mutation[] {
			(gene) -> gene.mutateXpr(),
			(gene) -> ((LayerGene) gene).mutateLayNum()
	};
	
	public double layerNum;
	
	public LayerGene(double xprLevel, double layerNum) {
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
	}
	
	public LayerGene(boolean positive, int layerNum) {
		this.xprLevel = ((positive) ? 1 : -1)*RNG.getHalfGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.5, 0.3);
	}
	
	public LayerGene(int layerNum) {
		this.xprLevel = RNG.getGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.5, 0.3);
	}
	
	private void mutateLayNum() {
		layerNum = Math.max(1.0, layerNum + RNG.getPseudoGauss(mMag));
	}

	@Override
	protected Gene clone() {
		return new LayerGene(xprLevel, layerNum);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(mutations, rand);
	}
	
	public String toString() {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(3);
		return "[" + format.format(xprLevel) + ", " + format.format(layerNum) + "]";
	}

}
