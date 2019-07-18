package utils;

import org.apache.commons.math3.util.Pair;

public class NodeTuple extends Pair<Integer, Integer> implements Comparable<NodeTuple>{

	
	public NodeTuple(Integer k, Integer v) {
		super(k, v);
	}
	
	public int layer() {
		return getKey();
	}
	
	public int node() {
		return getValue();
	}

	@Override
	public int compareTo(NodeTuple other) {
		if (layer() != other.layer()) return layer() - other.layer();
		else return node() - other.node();
	}



}
