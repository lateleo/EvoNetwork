package network;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import utils.CMUtils;
import utils.ConnTuple;
import utils.NodeTuple;
import utils.Stats;

public class Layer implements Runnable {
	Map<NodeTuple, Node> inputs;
	Map<NodeTuple, Double> normInputs;
	Map<Integer, Node> nodes = new TreeMap<>();
	
	public Layer(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights, NeuralNetwork network) {
		inputs = new Hashtable<NodeTuple,Node>();
		normInputs = new Hashtable<NodeTuple,Double>();
		fillInputs(inputConnWeights, network);
		fillNodes(nodeBiases, inputConnWeights);
	}
	
//	Only used for inheriting and overriding in child classes
	protected Layer(){}
	
	protected void fillInputs(Map<ConnTuple,Double> inputConnWeights, NeuralNetwork network) {
		inputConnWeights.keySet().forEach(tuple -> {
			Node inNode = network.get(tuple.iLay()).get(tuple.iNode());
			inputs.put(new NodeTuple(tuple), inNode);
		});
	}
	
	protected void fillNodes(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights) {
		nodeBiases.forEach((nodeNum, bias) -> {
			Map<NodeTuple,Double> weights = CMUtils.getConnsForNode(inputConnWeights, nodeNum);
			nodes.put(nodeNum, new Node(this, bias, weights));
		});
	}
	
	
	protected void normalize() {
		double mean = Stats.mean(inputs.values());
		double sigma = Stats.sigma(inputs.values());
		inputs.forEach((tuple, node) -> normInputs.put(tuple, (node.getOutput() - mean)/sigma));
	}
	
	public void run() {
		normalize();
		nodes.values().forEach(node -> node.run());
	}
	
	private Node get(int nodeNum) {
		return nodes.get(nodeNum);
	}

}
