package network;

import java.util.Map;

import ecology.Species;
import utils.ConnTuple;
import utils.NodeVector;

public class BottomLayer extends Layer {
	
	BottomLayer(NeuralNetwork network, Map<ConnTuple, Connection> conns) {
		super(network);
		for (NodeVector vector : NodeVector.bottomVectors) nodes.add(new BottomNode(network, conns, vector));
	}


}
