package genetics;

import staticUtils.MathUtils;
import staticUtils.RNG;
import utils.NodeTuple;
import utils.NodeVector;

/*
 * This class represents genes that determine the presence/absence and bias of nodes within a specific layer in a network.
 * 'xprLevel' is a value that roughly represents how strongly a gene is expressed,
 * 'layerNum' indicates the layer that the node is in,
 * 'nodeNum' indicates the specific node within the layer,
 * 'bias' indicates the node's individual bias.
 */
public class NodeGene extends Gene {
	private static Mutation[] mutations = new Mutation[] {
			(gene, mag) -> gene.mutateActivation(mag),
			(gene, mag) -> gene.mutateXpr(mag),
			(gene, mag) -> ((NodeGene) gene).mutateLayNum(mag),
			(gene, mag) -> ((NodeGene) gene).mutateNodeVector(mag),
			(gene, mag) -> ((NodeGene) gene).mutateBias(mag),
			(gene, mag) -> ((NodeGene) gene).mutateLearnFactor(mag)
	};
	
	public double layerNum, nodeX, nodeY, bias, learnFactor;
	 
	
	public NodeGene(double activation, double xprLevel, double layerNum, double nodeX, double nodeY, double bias, double learnFactor) {
		this.activation = activation;
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
		this.nodeX = nodeX;
		this.nodeY = nodeY;
		this.bias = bias;
		this.learnFactor = learnFactor;
	}
	
	public NodeGene(boolean posAct, boolean posXpr, NodeTuple tuple) {
		this.activation = ((posAct) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.xprLevel = ((posXpr) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.layerNum = tuple.layer() + RNG.getUnitPseudoGauss();
		this.nodeX = tuple.node().getX() + RNG.getUnitPseudoGauss();
		this.nodeY = tuple.node().getY() + RNG.getUnitPseudoGauss();
		this.bias = RNG.getPseudoGauss();
		this.learnFactor = -2-RNG.getHalfPseudoGauss();
	}
	
	public NodeGene(boolean posAct, boolean posXpr, int layerNum, NodeVector vector) {
		this.activation = ((posAct) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.xprLevel = ((posXpr) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.layerNum = layerNum + RNG.getUnitPseudoGauss();
		this.nodeX = vector.getX() + RNG.getUnitPseudoGauss();
		this.nodeY = vector.getY() + RNG.getUnitPseudoGauss();
		this.bias = RNG.getPseudoGauss();
		this.learnFactor = -2-RNG.getHalfPseudoGauss();
	}
	
	private void mutateLayNum(double mag) {
		layerNum = Math.max(-1.0, layerNum + RNG.getPseudoGauss(mag));
	}
	
	private void mutateNodeVector(double mag) {
		double vectorMag = RNG.getHalfPseudoGauss(mag);
		double theta = RNG.getDouble()*2*Math.PI;
		nodeX += vectorMag*MathUtils.cos(theta);
		nodeY += vectorMag*MathUtils.sin(theta);
	}
	
	private void mutateBias(double mag) {
		bias += RNG.getPseudoGauss(mag);
	}
	
	private void mutateLearnFactor(double mag) {
		learnFactor += RNG.getPseudoGauss(mag);
	}
	
	
	@Override
	protected Gene clone() {
		return new NodeGene(activation, xprLevel, layerNum, nodeX, nodeY, bias, learnFactor);
	}

	@Override
	public Gene mutate(double rand, double mag) {
		return mutate(mutations, rand, mag);
	}

}
