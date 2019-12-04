package network;

import java.util.Map;

import staticUtils.CMUtils;
import utils.ConnTuple;

public class BottomNode extends Node {
	NeuralNetwork network;
	private int nodeNum;

	BottomNode(NeuralNetwork network, Map<ConnTuple, Connection> conns, int nodeNum) {
		this.network = network;
		this.nodeNum = nodeNum;
		Map<ConnTuple, Connection> nodeConns = CMUtils.subMap(conns, (tuple) -> tuple.iNode() == nodeNum);
		for (Connection conn : nodeConns.values()) conn.setDownNode(this);
	}
	
	public void run() {
		output = network.currentImage.getValue(nodeNum);
	}	
	
}
