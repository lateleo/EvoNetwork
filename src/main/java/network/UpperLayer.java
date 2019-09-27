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
	public int layNum;
	
	public UpperLayer(Map<Integer,ConnSetPair> pairs, Map<ConnTuple,Double> weights, NeuralNetwork network, int layNum) {
		super(network);
		this.layNum = layNum;
		inputNodes = new Hashtable<NodeTuple,Node>();
		normInputs = new Hashtable<NodeTuple,Double>();
		fillInputs(weights, network);
	}
	
	
	protected void fillInputs(Map<ConnTuple,Double> weights, NeuralNetwork network) {
		for (ConnTuple tuple : weights.keySet()) {
			Node inNode = network.get(tuple.iLay()).nodes.get(tuple.iNode());
			inputNodes.put(tuple.getKey(), inNode);
		}
	}
	
	
	public Map<NodeTuple,Double> getConnsForNode(Map<ConnTuple,Double> source, ConnSetPair pair) {
		Map<NodeTuple,Double> weights = new TreeMap<>();
		for (ConnTuple cTuple : pair.downConns) {
			weights.put(cTuple.getFirst(), source.get(cTuple));
		}
		return weights;
	}

	protected void normalize() {
		double mean = Math.max(Double.MIN_NORMAL, Stats.meanOutput(inputNodes.values()));
		nanCheck(mean, "Normalization Mean in Layer " + layNum);
		double sigma = Math.max(Double.MIN_NORMAL, Stats.nodeSigma(inputNodes.values()));
		nanCheck(mean, "Normalization Sigma in Layer " + layNum);
		inputNodes.forEach((tuple, node) -> {
			double norm = (node.output - mean)/sigma;
			normInputs.put(tuple, norm);
			nanCheck(norm, "Normalized Input from " + tuple);
		});
	}
	
	public boolean nanCheck(double value, String message) {
		if (!network.nanFound && !Double.isFinite(value)) {
			System.out.println("NaN Found: " + message + "; Value: " + value);
			network.nanFound = true;
			return true;
		}
		return false;
	}
	

	@Override
	public void run() {
		normalize();
		super.run();
	}
	
	public void backProp() {
		nodes.forEach((nodeNum,node) -> ((UpperNode) node).backProp());
	}

}
