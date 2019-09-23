package genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ecology.Species;
import utils.RNG;

/*
 * This class represents genes that determine the presence/absence and bias of nodes within a specific layer in a network.
 * 'xprLevel' is a value that roughly represents how strongly a gene is expressed,
 * 'layerNum' indicates the layer that the node is in,
 * 'nodeNum' indicates the specific node within the layer,
 * 'bias' indicates the node's individual bias.
 */
public class NodeGene extends Gene {
	private static int topNodes = Species.topNodes;
	private static double mMag = Species.mutationMagnitude;
	public double xprLevel, layerNum, nodeNum, bias;
	Mutation xprMutation = (mutant) -> ((NodeGene) mutant).xprLevel += RNG.getShiftDouble()*mMag;
	Mutation layNumMutation = (gene) -> {
		NodeGene mutant = (NodeGene) gene;
		mutant.layerNum = Math.max(-1.0, mutant.layerNum + RNG.getShiftDouble()*mMag);
	};
	Mutation nodeNumMutation = (gene) -> {
		NodeGene mutant = (NodeGene) gene;
		mutant.nodeNum = Math.max(0.0, mutant.nodeNum + RNG.getShiftDouble()*mMag);
	};
	Mutation biasMutation = (mutant) -> ((NodeGene) mutant).bias += RNG.getGauss()*mMag;
	 
	
	public NodeGene(double xprLevel, double layerNum, double nodeNum, double bias) {
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
		this.nodeNum = nodeNum;
		this.bias = bias;
	}
	
	public NodeGene(boolean positive, int layerNum, int nodeNum) {
		this.xprLevel = ((positive) ? 1 : -1)*RNG.getHalfGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.3, 0.5);
		this.nodeNum = nodeNum + RNG.getBoundGauss(0, 1, 0.3, 0.5);
		this.bias = RNG.getGauss();
	}
	
	public NodeGene(int layerNum, int nodeNum) {
		this.xprLevel = RNG.getGauss();
		this.layerNum = layerNum + RNG.getBoundGauss(0, 1, 0.3, 0.5);
		this.nodeNum = nodeNum + RNG.getBoundGauss(0, 1, 0.3, 0.5);
		this.bias = RNG.getGauss();
	}
	
	
	@Override
	protected Gene clone() {
		return new NodeGene(this.xprLevel, this.layerNum, this.nodeNum, this.bias);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(new Mutation[] {xprMutation, layNumMutation, nodeNumMutation, biasMutation}, rand);
	}

}
