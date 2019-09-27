package network;

import java.util.Hashtable;
import java.util.Map;

import ecology.Species;
import utils.NodeTuple;

public abstract class UpperNode extends Node {
	protected static double learningRate = Species.learningRate;
	protected static int invBatchSize = Species.batchSize;
	
	protected UpperLayer layer;
	protected int nodeNum;
	protected double avgOutput = 0;
	protected double derivative = 0;
	Map<NodeTuple, Double> norms;
	Map<NodeTuple, Double> weights;
	Map<NodeTuple, Node> inputNodes;
	
	UpperNode(UpperLayer layer, int nodeNum, Map<NodeTuple, Double> weights){
		this.layer = layer;
		this.nodeNum = nodeNum;
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
		layer.nanCheck(output, "Node Output - Layer " + layer.layNum + ", Node " + nodeNum );
		avgOutput += output;
	}
	
	public void backProp() {
		avgOutput *= invBatchSize;
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
