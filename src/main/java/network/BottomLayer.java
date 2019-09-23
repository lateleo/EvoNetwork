package network;

import data.MnistImage;
import ecology.Species;

public class BottomLayer extends Layer {
	private static MnistImage[][] images = Species.images;
	private static int nodeNum = Species.bottomNodes;
	private static MnistImage[] currentImageSet = images[0];
	private static int currentBatchNum = 0;
	private static int imageCount = Species.batchSize;
	MnistImage currentImage;
	private int currentIndex = 0;

	
	BottomLayer() {
		for (int i = 0; i < nodeNum; i++) {
			nodes.put(i, new BottomNode(this, i));
		}
	}
	
	public static void nextBatch() {
		currentBatchNum = (currentBatchNum + 1) % images.length;
		currentImageSet = images[currentBatchNum];
	}
	
	public static void testBatch() {
		currentImageSet = Species.testImages;
	}

	@Override
	public void run() {
		currentImage = currentImageSet[currentIndex];
		nodes.values().forEach(node -> node.run());
		currentIndex++;
	}
	
	boolean allImagesComplete() {
		return currentIndex == imageCount;
	}
	
	public void resetImageIndex() {
		currentIndex = 0;
	}
	
	private class BottomNode extends Node {
		private BottomLayer layer;
		private int nodeNum;

		BottomNode(BottomLayer layer, int nodeNum) {
			this.layer = layer;
			this.nodeNum = nodeNum;
		}
		
		public void run() {
			output = layer.currentImage.getValue(nodeNum);
		}	
		
	}
}
