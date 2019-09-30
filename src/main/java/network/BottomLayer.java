package network;

import ecology.Species;

public class BottomLayer extends Layer {
//	private static MnistImage[][] images = Species.images;
	private static int nodeNum = Species.bottomNodes;
//	private static MnistImage[] currentImageSet = images[0];
//	private static int currentBatchNum = 0;
//	private static int imageCount = Species.batchSize;
//	private int currentIndex = 0;

	
	BottomLayer(NeuralNetwork network) {
		super(network);
		for (int i = 0; i < nodeNum; i++) {
			nodes.put(i, new BottomNode(network, i));
		}
	}

	private class BottomNode extends Node {
		NeuralNetwork network;
		private int nodeNum;

		BottomNode(NeuralNetwork network, int nodeNum) {
			this.network = network;
			this.nodeNum = nodeNum;
		}
		
		public void run() {
			output = network.currentImage.getValue(nodeNum);
		}	
		
	}
}
