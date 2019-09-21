package network;

import java.util.Map;

import ecology.Species;
import utils.NodeTuple;

public class Node extends AbstractNode {
	private double learningRate = Species.learningRate;
	private double bias;
	protected double derivative;
	Map<NodeTuple, Double> norms;
	Map<NodeTuple, Double> lowerWeights;
	Map<NodeTuple, Double> upperWeights;
	
	Node(Layer layer, double bias, Map<NodeTuple, Double> weights){
		this.norms = layer.normInputs;
		this.bias = bias;
		this.lowerWeights = weights;
	}

	public void run() {
		lowerWeights.forEach((key, value) -> output += value*norms.get(key));
		output = Math.max(0.0, output + bias);
	}
	
	public void backProp() {
//		upperWeights.forEach((key,value) -> {
//			if (upperNode.output > 0) {
//			derivative += value*upperNode.derivative;
//			}
//		});
		lowerWeights.forEach((key, value) -> value += learningRate*output*derivative);
		bias += learningRate*derivative;
	}
	

	

	
}
