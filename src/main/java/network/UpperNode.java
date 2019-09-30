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
	Map<NodeTuple, Conn> downConns;
//	Map<NodeTuple, Node> inputNodes;
	
	UpperNode(UpperLayer layer, int nodeNum, Map<NodeTuple, Conn> conns){
		this.layer = layer;
		this.nodeNum = nodeNum;
		this.norms = layer.normInputs;
		this.downConns = conns;
//		inputNodes = new Hashtable<>();
//		for (NodeTuple tuple : conns.keySet()) {
//			inputNodes.put(tuple, layer.inputNodes.get(tuple));
//		}
	}

	public void run() {
		downConns.forEach((tuple, conn) -> output += conn.weight*norms.get(tuple));
		output = Math.max(0.0, output);
		layer.nanCheck(output, "Node Output - Layer " + layer.layNum + ", Node " + nodeNum );
		avgOutput += output;
	}
	
	public void backProp() {
		avgOutput *= invBatchSize;
		downConns.forEach((tuple, conn) -> {
			conn.weight += learningRate*avgOutput*derivative;
//			if (tuple.layer() != 0) {
//				UpperNode inNode = (UpperNode) inputNodes.get(tuple);
//				inNode.derivative += conn.weight*derivative;
//			}
		});
		avgOutput = 0;
		derivative = 0;
	}
	
	public abstract void updateDerivative();

}
