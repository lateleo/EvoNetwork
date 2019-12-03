package network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Layer implements Runnable {
	List<Node> nodes = new ArrayList<>();
	NeuralNetwork network;
	
	Layer(NeuralNetwork network) {
		this.network = network;
	}

	@Override
	public void run() {
		for (Node node : nodes) node.run();
	}

}
