package utils;

import java.util.Comparator;

import org.apache.commons.math3.util.Pair;

import ecology.Species;
import staticUtils.ComparisonUtils;

/*
 * This class is used to represent "coordinates" of sorts for a given node.
 */
public class NodeTuple extends Pair<Integer, Integer> implements Comparable<NodeTuple>{
	
	public NodeTuple(Integer layer, Integer node) {
		super(layer, node);
	}
	
	public int layer() {
		return getKey();
	}
	
	public int node() {
		return getValue();
	}


	@Override
	public int compareTo(NodeTuple other) {
		return ComparisonUtils.compareNodeTuples(this, other);
	}
	
	public boolean equals(NodeTuple other) {
		return layer() == other.layer() && node() == other.node();
	}



}
