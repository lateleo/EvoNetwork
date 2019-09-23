package network;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import utils.ConnSetPair;
import utils.ConnTuple;
import utils.NodeTuple;
import utils.Stats;

public abstract class UpperLayer extends Layer {
	Map<NodeTuple, Node> inputNodes;
	Map<NodeTuple, Double> normInputs;
	
	public UpperLayer(Map<Integer,ConnSetPair> pairs, Map<ConnTuple,Double> weights, NeuralNetwork network) {
		inputNodes = new Hashtable<NodeTuple,Node>();
		normInputs = new Hashtable<NodeTuple,Double>();
		fillInputs(weights, network);
	}
	
	
	protected void fillInputs(Map<ConnTuple,Double> weights, NeuralNetwork network) {
		weights.keySet().forEach(tuple -> {
			Node inNode = network.get(tuple.iLay()).get(tuple.iNode());
			inputNodes.put(tuple.getKey(), inNode);
		});
	}
	
	
	public Map<NodeTuple,Double> getConnsForNode(Map<ConnTuple,Double> source, ConnSetPair pair) {
		Map<NodeTuple,Double> weights = new TreeMap<>();
		for (ConnTuple cTuple : pair.downConns) {
			weights.put(cTuple.getFirst(), source.get(cTuple));
		}
		return weights;
	}

	protected void normalize() {
		double mean = Stats.meanOutput(inputNodes.values());
		double sigma = Stats.sigma(inputNodes.values());
		inputNodes.forEach((tuple, node) -> normInputs.put(tuple, (node.getOutput() - mean)/sigma));
	}
	

	@Override
	public void run() {
		normalize();
		nodes.values().forEach(node -> node.run());
	}
	
	public void backProp() {
		nodes.forEach((nodeNum,node) -> ((UpperNode) node).backProp());
	}

}
