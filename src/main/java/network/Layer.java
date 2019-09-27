package network;

import java.util.Map;
import java.util.TreeMap;

public abstract class Layer implements Runnable {
	Map<Integer, Node> nodes = new TreeMap<>();
	NeuralNetwork network;
	
	Layer(NeuralNetwork network) {
		this.network = network;
	}

	@Override
	public void run() {
		nodes.values().forEach(node -> node.run());
	}

}
