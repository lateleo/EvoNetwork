package network;

import ecology.Species;

public class BottomLayer extends Layer {
	private static int nodeCount = Species.bottomNodes;
	
	BottomLayer() {
		for (int i = 0; i < nodeCount; i++) {
			nodes.put(i, new BottomNode());
		}
	}

	@Override
	public void run() {
		nodes.values().forEach(node -> node.run());
	}
	
	private class BottomNode extends Node {
//		TODO: finish this when more info about image processing is given

		BottomNode() {
		}
		
		public void run() {
			
		}
	}
}
