package network;

import data.MnistImage;
import ecology.Species;

public class BottomLayer extends AbstractLayer {
	private static MnistImage[] images = Species.images;
	private static int imageCount = images.length;
	private static int nodeNum = Species.bottomNodes;
	MnistImage currentImage;
	private int currentIndex = 0;

	
	BottomLayer() {
		for (int i = 0; i < nodeNum; i++) {
			nodes.put(i, new BottomNode(this,i));
		}
	}

	@Override
	public void run() {
		currentImage = images[currentIndex];
		nodes.values().forEach(node -> node.run());
		currentIndex++;
	}
	
	boolean allImagesComplete() {
		return currentIndex == imageCount;
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
