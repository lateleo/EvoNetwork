package network;

import java.util.Map;

import utils.NodeTuple;

public class Node extends AbstractNode {
	private double bias;
	private double derivative;
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
	
	public void backProp(float learning_rate) {
		//Step Function 
		if (uppernodeoutput > 0) {
			upperweights.forEach((key,value) -> if (uppernodeoutput > 0) { 
					(derivative += value*uppernodederivative);
					}
			}
		weights.forEach((key, value) -> value += learning_rate*output*derivative);
		bias += learning_rate*derivative;
			}	
	}
	// Backprop for Top Layer Nodes
	public void topBackProp(double error) {
		derivative = 2*error;
	}
	

	
}
