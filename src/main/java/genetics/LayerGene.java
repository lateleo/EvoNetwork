package genetics;

import java.text.DecimalFormat;

import ecology.Species;
import staticUtils.RNG;

/*
 * This class represents genes that determine the presence/absence of additional layers in a network.
 * 'xprLevel' is a value that roughly represents how strongly a gene is expressed,
 * and 'layerNum' indicates which layer the gene encodes.
 */
public class LayerGene extends Gene {
	private static double mMag = Species.mutationMagnitude;
	public double xprLevel, layerNum;
	Mutation xprMutation = (mutant) -> ((LayerGene) mutant).xprLevel += RNG.getShiftDouble()*mMag;
	Mutation layNumMutation = (gene) -> {
		LayerGene mutant = (LayerGene) gene;
		mutant.layerNum = Math.max(1.0, mutant.layerNum + RNG.getShiftDouble()*mMag);
	};
	
	public LayerGene(double xprLevel, double layerNum) {
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
	}
	
	public LayerGene(boolean positive, int layerNum) {
		this.xprLevel = ((positive) ? 1 : -1)*RNG.getHalfGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.3, 0.5);
	}
	
	public LayerGene(int layerNum) {
		this.xprLevel = RNG.getGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.3, 0.5);
	}
	


	@Override
	protected Gene clone() {
		return new LayerGene(this.xprLevel, this.layerNum);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(new Mutation[] {xprMutation, layNumMutation}, rand);
	}
	
	public String toString() {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(3);
		return "[" + format.format(xprLevel) + ", " + format.format(layerNum) + "]";
	}

}
