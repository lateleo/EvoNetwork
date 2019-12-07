package network;

import java.util.Map;

import staticUtils.CMUtils;
import utils.ConnTuple;
import utils.NodeVector;

public class BottomNode extends Node {
	NeuralNetwork network;
	private NodeVector vector;

	BottomNode(NeuralNetwork network, Map<ConnTuple, Connection> conns, NodeVector vector) {
		this.network = network;
		this.vector = vector;
		Map<ConnTuple, Connection> nodeConns = CMUtils.subMap(conns, (tuple) -> tuple.iNode() == vector);
		for (Connection conn : nodeConns.values()) conn.setDownNode(this);
	}
	
	public void run() {
		output = network.currentImage.getValue(vector);
	}	
	
}
