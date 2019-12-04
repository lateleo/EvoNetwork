package network;

import java.util.Map;

import ecology.Species;
import utils.ConnTuple;

public class BottomLayer extends Layer {
	private static int nodeNum = Species.bottomNodes;


	
	BottomLayer(NeuralNetwork network, Map<ConnTuple, Connection> conns) {
		super(network);
		for (int i = 0; i < nodeNum; i++) {
			nodes.add(new BottomNode(network, conns, i));
		}
	}


}
