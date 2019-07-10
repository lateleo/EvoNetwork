package utils;

import java.util.Collection;

import org.apache.commons.math3.stat.StatUtils;

import network.Node;

public class Stats {
	
	public static double mean(Collection<Node> vals) {
		double[] ary = vals.stream().mapToDouble(node -> node.getOutput()).toArray();
		return StatUtils.mean(ary);
	}
	
	public static double sigma(Collection<Node> vals) {
		double[] ary = vals.stream().mapToDouble(node -> node.getOutput()).toArray();
		return Math.sqrt(StatUtils.populationVariance(ary));
	}

}
