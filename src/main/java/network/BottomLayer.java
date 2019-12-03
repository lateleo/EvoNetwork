package network;

import java.util.Map;

import ecology.Species;
import staticUtils.CMUtils;
import utils.ConnTuple;

public class BottomLayer extends Layer {
//	private static MnistImage[][] images = Species.images;
	private static int nodeNum = Species.bottomNodes;
//	private static MnistImage[] currentImageSet = images[0];
//	private static int currentBatchNum = 0;
//	private static int imageCount = Species.batchSize;
//	private int currentIndex = 0;

	
	BottomLayer(NeuralNetwork network, Map<ConnTuple, Conn> conns) {
		super(network);
		for (int i = 0; i < nodeNum; i++) {
			nodes.add(new BottomNode(network, conns, i));
		}
	}

	private class BottomNode extends Node {
		NeuralNetwork network;
		private int nodeNum;

		BottomNode(NeuralNetwork network, Map<ConnTuple, Conn> conns, int nodeNum) {
			this.network = network;
			this.nodeNum = nodeNum;
			Map<ConnTuple, Conn> nodeConns = CMUtils.subMap(conns, (tuple) -> tuple.iNode() == nodeNum);
			for (Conn conn : nodeConns.values()) conn.setDownNode(this);
		}
		
		public void run() {
			output = network.currentImage.getValue(nodeNum);
		}	
		
	}
}
