package genetics;

import staticUtils.RNG;
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
			(gene) -> gene.mutateXpr(),
			(gene) -> ((NodeGene) gene).mutateLayNum(),
			(gene) -> ((NodeGene) gene).mutateNodeVector(),
			(gene) -> ((NodeGene) gene).mutateBias(),
			(gene) -> ((NodeGene) gene).mutateLearnFactor()
	};
	
	public double layerNum, bias, learnFactor;
	public NodeVector nodeVector;
	 
	
	public NodeGene(double xprLevel, double layerNum, NodeVector nodeVector, double bias, double learnFactor) {
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
		this.nodeVector = nodeVector;
		this.bias = bias;
		this.learnFactor = learnFactor;
	}
	
	public NodeGene(boolean positive, int layerNum, int nodeX, int nodeY) {
		this.xprLevel = ((positive) ? 1 : -1)*RNG.getHalfGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		double x = nodeX + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		double y = nodeY + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.nodeVector = new NodeVector(x,y);
		this.bias = RNG.getGauss();
		this.learnFactor = RNG.getGauss();
	}
	
	public NodeGene(int layerNum, int nodeX, int nodeY) {
		this.xprLevel = RNG.getGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		double x = nodeX + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		double y = nodeY + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.nodeVector = new NodeVector(x,y);
		this.bias = RNG.getGauss();
		this.learnFactor = RNG.getGauss();
	}
	
	private void mutateLayNum() {
		layerNum = Math.max(-1.0, layerNum + RNG.getPseudoGauss(mMag));
	}
	
	private void mutateNodeVector() {
		double mag = RNG.getHalfPseudoGauss(mMag);
		double theta = RNG.getDouble()*2*Math.PI;
		nodeVector.add(mag, theta);
	}
	
	private void mutateBias() {
		bias += RNG.getGauss(mMag);
	}
	
	private void mutateLearnFactor() {
		learnFactor += RNG.getGauss(mMag);
	}
	
	
	@Override
	protected Gene clone() {
		return new NodeGene(xprLevel, layerNum, nodeVector.clone(), bias, learnFactor);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(mutations, rand);
	}

}
