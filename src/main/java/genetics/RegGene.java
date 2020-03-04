package genetics;

import staticUtils.RNG;

public class RegGene extends Gene {
	private static Mutation[] mutations = new Mutation[] {
			(gene, mag) -> gene.mutateActivation(mag),
			(gene, mag) -> gene.mutateXpr(mag),
			(gene, mag) -> ((RegGene) gene).mutateRateFactor(mag),
			(gene, mag) -> ((RegGene) gene).mutateMagFactor(mag),
			(gene, mag) -> ((RegGene) gene).mutateSlipFactor(mag)
	};
	
	public double rateFactor, magFactor, slipFactor;
	
	public RegGene(double activation, double xprLevel, double rateFactor, double magFactor, double slipFactor) {
		this.activation = activation;
		this.xprLevel = xprLevel;
		this.rateFactor = rateFactor;
		this.magFactor = magFactor;
		this.slipFactor = slipFactor;
	}
	
	public RegGene(boolean posAct, double rateFactor, double magFactor, double slipFactor) {
		this.activation = ((posAct) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.xprLevel = 1 + RNG.getHalfPseudoGauss();
		this.rateFactor = rateFactor + RNG.getPseudoGauss();
		this.magFactor = magFactor + RNG.getPseudoGauss();
		this.slipFactor = slipFactor + RNG.getPseudoGauss();
	}
	
	private void mutateRateFactor(double mag) {
		rateFactor += RNG.getPseudoGauss(mag);
	}
	
	private void mutateMagFactor(double mag) {
		magFactor += RNG.getPseudoGauss(mag);
	}
	
	private void mutateSlipFactor(double mag) {
		slipFactor += RNG.getPseudoGauss(mag);
	}

	@Override
	protected Gene clone() {
		return new RegGene(activation, xprLevel, rateFactor, magFactor, slipFactor);
	}

	@Override
	public Gene mutate(double rand, double mag) {
		return mutate(mutations, rand, mag);
	}

}
