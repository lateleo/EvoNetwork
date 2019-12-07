package utils;

import staticUtils.ComparisonUtils;

/*
 * This class is used to represent "coordinates" of sorts for a given node.
 */
public class NodeTuple implements Comparable<NodeTuple>{
	private int layer;
	private NodeVector node;
	
	public NodeTuple(int layer, NodeVector node) {
		this.layer = layer;
		this.node = node;
	}
	
	public int layer() {
		return layer;
	}
	
	public NodeVector node() {
		return node;
	}


	@Override
	public int compareTo(NodeTuple other) {
		return ComparisonUtils.compareNodeTuples(this, other);
	}
	
	public boolean equals(NodeTuple other) {
		return layer() == other.layer() && node() == other.node();
	}



}
