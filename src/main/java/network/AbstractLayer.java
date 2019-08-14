package network;

import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractLayer implements Runnable {
	Map<Integer, AbstractNode> nodes = new TreeMap<>();
	
	protected AbstractNode get(int nodeNum) {
		return nodes.get(nodeNum);
	}

	@Override
	public abstract void run();
	
	public abstract void backProp();

}
