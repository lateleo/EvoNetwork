package network;

import java.util.Map;

import utils.NodeTuple;

public class Node extends AbstractNode {
	private double bias;
	Map<NodeTuple, Double> norms;
	Map<NodeTuple, Double> weights;
	
	Node(Layer layer, double bias, Map<NodeTuple, Double> weights){
		this.norms = layer.normInputs;
		this.bias = bias;
		this.weights = weights;
	}

	public void run() {
		weights.forEach((key, value) -> output += value*norms.get(key));
		output = Math.max(0.0, output + bias);
	}
	
	
	
}
