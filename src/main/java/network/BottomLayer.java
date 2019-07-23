package network;

import data.MnistImage;
import ecology.Species;

public class BottomLayer extends AbstractLayer {
	private static MnistImage[][] images = Species.images;
	private static int imageCount = images[0].length;
	private static int nodeNum = Species.bottomNodes;
	private static int currentEpoch = 0;
	MnistImage currentImage;
	private int currentIndex = 0;

	
	BottomLayer() {
		for (int i = 0; i < nodeNum; i++) {
			nodes.put(i, new BottomNode(this,i));
		}
	}
	
	public static void nextEpoch() {
		currentEpoch = (currentEpoch + 1) % images.length;
	}

	@Override
	public void run() {
		currentImage = images[currentEpoch][currentIndex];
		nodes.values().forEach(node -> node.run());
		currentIndex++;
	}
	
	boolean allImagesComplete() {
		return currentIndex == imageCount;
	}
	
	public void resetIndex() {
		currentIndex = 0;
	}
	
	private class BottomNode extends AbstractNode {
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
