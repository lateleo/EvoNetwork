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
			(gene, mag) -> gene.mutateActivation(mag),
			(gene, mag) -> gene.mutateXpr(mag),
			(gene, mag) -> ((LayerGene) gene).mutateLayNum(mag)
	};
	
	public double layerNum;
	
	public LayerGene(double activation, double xprLevel, double layerNum) {
		this.activation = activation;
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
	}
	
	public LayerGene(boolean posAct, boolean posXpr, int layerNum) {
		this.activation = ((posAct) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.xprLevel = ((posXpr) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.layerNum = layerNum + RNG.getUnitPseudoGauss();
	}
	
	private void mutateLayNum(double mag) {
		layerNum = Math.max(1.0, layerNum + RNG.getPseudoGauss(mag));
	}

	@Override
	protected Gene clone() {
		return new LayerGene(activation, xprLevel, layerNum);
	}

	@Override
	public Gene mutate(double rand, double mag) {
		return mutate(mutations, rand, mag);
	}
	
	public String toString() {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(3);
		return "[" + format.format(activation) + ", " + format.format(xprLevel) + ", " + format.format(layerNum) + "]";
	}

}
