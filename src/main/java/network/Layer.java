package network;

import java.util.Map;
import java.util.TreeMap;

public abstract class Layer implements Runnable {
	Map<Integer, Node> nodes = new TreeMap<>();
	
	protected Node get(int nodeNum) {
		return nodes.get(nodeNum);
	}

	@Override
	public abstract void run();

}
