package utils;

import java.util.Comparator;

import org.apache.commons.math3.util.Pair;

import ecology.Species;

/*
 * This class is used to represent "coordinates" of sorts for a given node.
 */
public class NodeTuple extends Pair<Integer, Integer> implements Comparable<NodeTuple>{
	private static Comparator<Integer> comparator = Species.comparator;
	
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
		int layComp = comparator.compare(layer(), other.layer());
		if (layComp == 0) return node() - other.node();
		else return layComp;
	}
	
	public boolean equals(NodeTuple other) {
		return layer() == other.layer() && node() == other.node();
	}



}
