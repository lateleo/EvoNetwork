package network;

import java.util.Collection;
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
		fillInputs(nodePhenes);
		fillNodes(nodePhenes, conns);
	}
	
	
	protected void fillInputs(Map<Integer, NodePhene> nodePhenes) {
		nodePhenes.forEach((nodeNum, phene) -> {
			phene.downConns.forEach(tuple -> {
				Node inNode = network.get(tuple.iLay()).nodes.get(tuple.iNode());
				inputNodes.put(tuple.getKey(), inNode);
			});
		});
	}
	
	protected void fillNodes(Map<Integer, NodePhene> nodePhenes, Map<ConnTuple, Conn> conns) {
		nodePhenes.forEach((nodeNum, phene) -> {
			nodes.put(nodeNum, addNode(nodeNum, phene, conns));
		});
	}
	
	protected abstract UpperNode addNode(int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns);
	

	protected void normalize() {
//		inputNodes.forEach((tuple,node) -> {
//			normInputs.put(tuple, node.output);
//		});
		double mean = Stats.meanOutput(inputNodes.values());
		nanCheck(mean, "Normalization Mean in Layer " + layNum);
		nanCheck(mean*mean, "Norm Mean Squared in Layer " + layNum);
		double invSigma = Math.max(Double.MIN_NORMAL, 1/Stats.nodeSigma(inputNodes.values()));
		if (nanCheck(invSigma, "Normalization invSigma in Layer " + layNum)) {
			double partialSum = 0;
			System.out.println("Mean: " + mean);
			for (Node inNode : inputNodes.values()) {
				double sqr = Math.pow(inNode.output - mean, 2.0);
				partialSum += sqr;
//				System.out.println(inNode.output + ": " + sqr);
			}
			System.out.println("PartialSum: " + partialSum);
			double var = partialSum/inputNodes.size();
			System.out.println("Variance: " + var);
			System.out.println("Sigma: " + Math.sqrt(var));

		}
		inputNodes.forEach((tuple, node) -> {
			double norm = (node.output - mean)*invSigma;
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
