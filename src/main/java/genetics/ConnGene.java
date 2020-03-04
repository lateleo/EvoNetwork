package genetics;

import staticUtils.MathUtils;
import staticUtils.RNG;
import utils.ConnTuple;
import utils.NodeVector;

/*
 * This class represents genes that determine the presence/absence and weight of connections between nodes in a network.
 * 'xprLevel' is a value that roughly represents how strongly a gene is expressed,
 * 'inLayNum' and 'inNodeNum' indicate the layer and node numbers, respectively, of the node whose output is to be used,
 * 'outLayNum' and 'outNodeNum' indicate the layer and node numbers, respectively, of the node taking in said output,
 * 'weight' indicates the connection's weight value.
 */
public class ConnGene extends Gene {
	private static Mutation[] mutations = new Mutation[] {
			(gene, mag) -> gene.mutateActivation(mag),
			(gene, mag) -> gene.mutateXpr(mag),
			(gene, mag) -> ((ConnGene) gene).mutateInputLayer(mag),
			(gene, mag) -> ((ConnGene) gene).mutateOutputLayer(mag),
			(gene, mag) -> ((ConnGene) gene).mutateInputVector(mag),
			(gene, mag) -> ((ConnGene) gene).mutateOutputVector(mag),
			(gene, mag) -> ((ConnGene) gene).mutateWeight(mag)
	};
	
	public double inLayNum, inNodeX, inNodeY, outLayNum, outNodeX, outNodeY, weight;
	
	
	public ConnGene(double activation, double xprLevel, double inLayNum, double inNodeX, double inNodeY, double outLayNum, double outNodeX, double outNodeY, double weight) {
		this.activation = activation;
		this.xprLevel = xprLevel;
		this.inLayNum = inLayNum;
		this.inNodeX = inNodeX;
		this.inNodeY = inNodeY;
		this.outLayNum = outLayNum;
		this.outNodeX = outNodeX;
		this.outNodeY = outNodeY;
		this.weight = weight;
	}
	
	public ConnGene(boolean posAct, boolean posXpr, ConnTuple tuple) {
		this.activation = ((posAct) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.xprLevel = ((posXpr) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.inLayNum = tuple.iLay() + RNG.getUnitPseudoGauss();
		this.inNodeX = tuple.iNode().getX() + RNG.getUnitPseudoGauss();
		this.inNodeY = tuple.iNode().getY() + RNG.getUnitPseudoGauss();
		this.outLayNum = tuple.oLay() + RNG.getUnitPseudoGauss();
		this.outNodeX = tuple.oNode().getX() + RNG.getUnitPseudoGauss();
		this.outNodeY = tuple.oNode().getY() + RNG.getUnitPseudoGauss();
		this.weight = RNG.getGauss();
	}
	
	private void mutateInputLayer(double mag) {
		inLayNum = Math.max(0.0, inLayNum + RNG.getPseudoGauss(mag));
	}
	
	private void mutateOutputLayer(double mag) {
		 outLayNum = Math.max(0.0, outLayNum + RNG.getPseudoGauss(mag));
	}
	
	private void mutateInputVector(double mag) {
		double vectorMag = RNG.getHalfPseudoGauss(mag);
		double theta = RNG.getDouble()*2*Math.PI;
		inNodeX += vectorMag*MathUtils.cos(theta);
		inNodeY += vectorMag*MathUtils.sin(theta);
	}
	
	private void mutateOutputVector(double mag) {
		double vectorMag = RNG.getHalfPseudoGauss(mag);
		double theta = RNG.getDouble()*2*Math.PI;
		outNodeX += vectorMag*MathUtils.cos(theta);
		outNodeY += vectorMag*MathUtils.sin(theta);
	}
	
	private void mutateWeight(double mag) {
		weight += RNG.getPseudoGauss(mag);
	}

	@Override
	protected Gene clone() {
		return new ConnGene(activation, xprLevel, inLayNum, inNodeX, inNodeY, outLayNum, outNodeX, outNodeY, weight);
	}

	@Override
	public Gene mutate(double rand, double mag) {
		return mutate(mutations, rand, mag);
	}

}
