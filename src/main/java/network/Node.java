package network;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import utils.CMUtils;
import utils.NodeTuple;

public class Node implements Runnable {
	private double output;
	private double bias;
	Map<NodeTuple, Double> norms;
	Map<NodeTuple, Double> weights;
	
	Node(Layer layer, double bias, Map<NodeTuple, Double> weights){
		this.norms = layer.normInputs;
		this.bias = bias;
		this.weights = weights;
	}
	
//	Only used for inheriting and overriding in child classes
	protected Node(){}
	
	
	public double getOutput() {
		return output;
	}

	public void run() {
		weights.forEach((key, value) -> output += value*norms.get(key));
		output = Math.max(0.0, output + bias);
	}
	
	
	
}
