package genetics;

import java.util.Arrays;
import utils.RNG;

public class NodeGene extends Gene {
	double xprLevel, layerNum, nodeNum, bias;
	Mutation xprMutation = (mutant) -> ((NodeGene) mutant).xprLevel += RNG.getShiftDouble();
	Mutation layNumMutation = (gene) -> {
		NodeGene mutant = (NodeGene) gene;
		mutant.layerNum = Math.max(-1.0, mutant.layerNum + RNG.getShiftDouble());
	};
	Mutation NodeNumMutation = (gene) -> {
		NodeGene mutant = (NodeGene) gene;
		mutant.nodeNum = Math.max(0.0, mutant.nodeNum + RNG.getShiftDouble());
	};
	
	public NodeGene(double xprLevel, double layerNum, double nodeNum, double bias) {
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
		this.nodeNum = nodeNum;
		this.bias = bias;
	}
	
	@Override
	protected Gene clone() {
		return new NodeGene(this.xprLevel, this.layerNum, this.nodeNum, this.bias);
	}

	@Override
	public Gene mutate() {
		return mutate(Arrays.asList(xprMutation, layNumMutation, NodeNumMutation));
	}

}
