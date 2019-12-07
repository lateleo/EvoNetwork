package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;
import utils.NodeVector;

public abstract class UpperLayer extends Layer {
	protected int layNum;
	
	public UpperLayer(Map<NodeVector,NodePhene> nodePhenes, Map<ConnTuple,Connection> conns, NeuralNetwork network, int layNum) {
		super(network);
		this.layNum = layNum;
		fillNodes(nodePhenes, conns);
	}

	
	protected void fillNodes(Map<NodeVector, NodePhene> nodePhenes, Map<ConnTuple, Connection> conns) {
		for (Map.Entry<NodeVector, NodePhene> entry : nodePhenes.entrySet()) {
			nodes.add(addNode(entry.getKey(), entry.getValue(), conns));
		}
	}
	
	protected abstract UpperNode addNode(NodeVector vector, NodePhene phene, Map<ConnTuple, Connection> conns);

	
	public boolean nanCheck(double value, String message) {
		if (!network.nanFound && !Double.isFinite(value)) {
			System.out.println("NaN Found: " + message + "; Value: " + value);
			network.nanFound = true;
			return true;
		}
		return false;
	}
	
	public void backProp() {
		for (Node node : nodes) ((UpperNode) node).backProp();
	}
	
	
	public int layNum() {
		return layNum;
	}

}
