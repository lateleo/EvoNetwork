package network;

import java.util.Hashtable;
import java.util.Map;

import ecology.Species;
import utils.NodeTuple;

public abstract class UpperNode extends Node {
	protected static double learningRate = Species.learningRate;
	protected static int batchSize = Species.batchSize;
	protected double avgOutput = 0;
	protected double derivative = 0;
	Map<NodeTuple, Double> norms;
	Map<NodeTuple, Double> weights;
	Map<NodeTuple, Node> inputNodes;
	
	UpperNode(UpperLayer layer, Map<NodeTuple, Double> weights){
		this.norms = layer.normInputs;
		this.weights = weights;
		inputNodes = new Hashtable<>();
		for (NodeTuple tuple : weights.keySet()) {
			inputNodes.put(tuple, layer.inputNodes.get(tuple));
		}
	}

	public void run() {
		weights.forEach((tuple, weight) -> output += weight*norms.get(tuple));
		output = Math.max(0.0, output);
		avgOutput += output;
	}
	
	public void backProp() {
		avgOutput /= batchSize;
		weights.forEach((tuple, weight) -> {
			weight += learningRate*avgOutput*derivative;
			if (tuple.layer() != 0) {
				UpperNode inNode = (UpperNode) inputNodes.get(tuple);
				inNode.derivative += weight*derivative;
			}
		});
		derivative = 0;
	}

}
