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
	
	public NodeGene(boolean positive, int layerNum, NodeVector vector) {
		this.xprLevel = ((positive) ? 1 : -1)*RNG.getHalfPseudoGauss();
		this.layerNum = layerNum + RNG.getUnitPseudoGauss();
		double x = vector.getX() + RNG.getUnitPseudoGauss();
		double y = vector.getY() + RNG.getUnitPseudoGauss();
		this.nodeVector = new NodeVector(x,y);
		this.bias = RNG.getPseudoGauss();
		this.learnFactor = RNG.getPseudoGauss();
	}
	
	public NodeGene(int layerNum, NodeVector vector) {
		this.xprLevel = RNG.getPseudoGauss();
		this.layerNum = layerNum + RNG.getUnitPseudoGauss();
		double x = vector.getX() + RNG.getUnitPseudoGauss();
		double y = vector.getY() + RNG.getUnitPseudoGauss();
		this.nodeVector = new NodeVector(x,y);
		this.bias = RNG.getPseudoGauss();
		this.learnFactor = RNG.getPseudoGauss();
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
		bias += RNG.getPseudoGauss(mMag);
	}
	
	private void mutateLearnFactor() {
		learnFactor += RNG.getPseudoGauss(mMag);
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
