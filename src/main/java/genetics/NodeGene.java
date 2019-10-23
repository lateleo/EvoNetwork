package genetics;

import staticUtils.RNG;

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
			(gene) -> ((NodeGene) gene).mutateNodeNum(),
			(gene) -> ((NodeGene) gene).mutateBias(),
			(gene) -> ((NodeGene) gene).mutateLearnFactor()
	};
	
	public double layerNum, nodeNum, bias, learnFactor;
	 
	
	public NodeGene(double xprLevel, double layerNum, double nodeNum, double bias, double learnFactor) {
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
		this.nodeNum = nodeNum;
		this.bias = bias;
		this.learnFactor = learnFactor;
	}
	
	public NodeGene(boolean positive, int layerNum, int nodeNum) {
		this.xprLevel = ((positive) ? 1 : -1)*RNG.getHalfGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.nodeNum = nodeNum + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.bias = RNG.getGauss();
		this.learnFactor = RNG.getGauss();
	}
	
	public NodeGene(int layerNum, int nodeNum) {
		this.xprLevel = RNG.getGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.nodeNum = nodeNum + RNG.getBoundGauss(0, 1, 0.5, 0.3);
		this.bias = RNG.getGauss();
		this.learnFactor = RNG.getGauss();
	}
	
	private void mutateLayNum() {
		layerNum = Math.max(-1.0, layerNum + RNG.getShiftDouble(mMag));
	}
	
	private void mutateNodeNum() {
		nodeNum = Math.max(0.0, nodeNum + RNG.getShiftDouble(mMag));
	}
	
	private void mutateBias() {
		bias += RNG.getGauss(mMag);
	}
	
	private void mutateLearnFactor() {
		learnFactor += RNG.getGauss(mMag);
	}
	
	
	@Override
	protected Gene clone() {
		return new NodeGene(this.xprLevel, this.layerNum, this.nodeNum, this.bias, this.learnFactor);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(mutations, rand);
	}

}
