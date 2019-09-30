package network;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import genetics.NodePhene;
import staticUtils.Stats;
import utils.ConnTuple;
import utils.NodeTuple;

public abstract class UpperLayer extends Layer {
	Map<NodeTuple, Node> inputNodes;
	Map<NodeTuple, Double> normInputs;
	public int layNum;
	
	public UpperLayer(Map<Integer,NodePhene> nodePhenes, Map<ConnTuple,Conn> conns, NeuralNetwork network, int layNum) {
		super(network);
		this.layNum = layNum;
		inputNodes = new Hashtable<NodeTuple,Node>();
		normInputs = new Hashtable<NodeTuple,Double>();
		fillInputs(conns, network);
		fillNodes(nodePhenes, conns);
	}
	
	
	protected void fillInputs(Map<ConnTuple,Conn> conns, NeuralNetwork network) {
		for (ConnTuple tuple : conns.keySet()) {
			Node inNode = network.get(tuple.iLay()).nodes.get(tuple.iNode());
			inputNodes.put(tuple.getKey(), inNode);
		}
	}
	
	protected void fillNodes(Map<Integer, NodePhene> nodePhenes, Map<ConnTuple, Conn> conns) {
		nodePhenes.forEach((nodeNum, phene) -> {
			Map<NodeTuple, Conn> nodeConns = getConnsForNode(conns, phene);
			nodes.put(nodeNum, addNode(nodeNum, phene, nodeConns));
		});
	}
	
	protected abstract UpperNode addNode(int nodeNum, NodePhene phene, Map<NodeTuple, Conn> nodeConns);
	
	
	public Map<NodeTuple,Conn> getConnsForNode(Map<ConnTuple,Conn> source, NodePhene pair) {
		Map<NodeTuple,Conn> conns = new TreeMap<>();
		for (ConnTuple cTuple : pair.downConns) {
			conns.put(cTuple.getFirst(), source.get(cTuple));
		}
		return conns;
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
