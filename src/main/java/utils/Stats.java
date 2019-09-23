package utils;

import java.util.Collection;

import org.apache.commons.math3.stat.StatUtils;

import network.Node;
import network.Organism;

public class Stats {
	
	public static double meanOutput(Collection<Node> vals) {
		double[] ary = vals.stream().mapToDouble(node -> node.getOutput()).toArray();
		return StatUtils.mean(ary);
	}
	
	public static double sigma(Collection<Node> vals) {
		double[] ary = vals.stream().mapToDouble(node -> node.getOutput()).toArray();
		return Math.sqrt(StatUtils.populationVariance(ary));
	}
	
	public static double meanAccuracy(Collection<Organism> orgs) {
		double[] ary = orgs.stream().mapToDouble(org -> org.getNetwork().getAccuracy()).toArray();
		return StatUtils.mean(ary);
	}

}
