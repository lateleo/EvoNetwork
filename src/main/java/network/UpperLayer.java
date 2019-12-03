package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;

public abstract class UpperLayer extends Layer {
	public int layNum;
	
	public UpperLayer(Map<Integer,NodePhene> nodePhenes, Map<ConnTuple,Conn> conns, NeuralNetwork network, int layNum) {
		super(network);
		this.layNum = layNum;
		fillNodes(nodePhenes, conns);
	}

	
	protected void fillNodes(Map<Integer, NodePhene> nodePhenes, Map<ConnTuple, Conn> conns) {
		nodePhenes.forEach((nodeNum, phene) -> nodes.add(addNode(nodeNum, phene, conns)));
	}
	
	protected abstract UpperNode addNode(int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns);

	
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

}
