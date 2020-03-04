package network;

import java.util.Map;

import ecology.Species;
import staticUtils.CMUtils;
import utils.ConnTuple;
import utils.NodeVector;

public class BottomNode extends Node {
	private static int minBottom = Species.minBottom;
	NeuralNetwork network;
	private int posX;
	private int posY;

	BottomNode(NeuralNetwork network, Map<ConnTuple, Connection> conns, NodeVector vector) {
		this.network = network;
		this.posX = ((int) vector.getX()) - minBottom;
		this.posY = ((int) vector.getY()) - minBottom;
		Map<ConnTuple, Connection> nodeConns = CMUtils.subMap(conns, (tuple) -> tuple.iNode() == vector);
		for (Connection conn : nodeConns.values()) conn.setDownNode(this);
	}
	
	public void run() {
		output = network.currentImage.getValue(posX, posY);
	}	
	
}
