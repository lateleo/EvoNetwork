package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import ecology.Species;
import network.Node;
import network.Organism;

public class Stats {
	
	public static double meanOutput(Collection<Node> vals) {
		double[] array = vals.stream().mapToDouble(node -> node.output).toArray();
		return StatUtils.mean(array);
	}
	
	public static double nodeSigma(Collection<Node> vals) {
		double[] outputs = vals.stream().mapToDouble(node -> node.output).toArray();
		return Math.sqrt(StatUtils.populationVariance(outputs));
	}
	
	public static SimpleRegression getRegression(List<Organism> orgs) {
		List<double[]> data = new ArrayList<double[]>();
		for (Organism org : orgs) {
			double[] pair = new double[2];
			pair[0] = org.getAgeLog();
			pair[1] = org.getPerformance();
			data.add(pair);
		}
		double[][] dataArray = data.toArray(new double[0][0]);
		SimpleRegression regression = new SimpleRegression(false);
		regression.addData(dataArray);
		return regression;
	}
	
	public static double getFitnessSigma(List<Organism> orgs) {
		double[] deltas = orgs.stream().mapToDouble(org -> org.getFitness()).toArray();
		return Math.sqrt(StatUtils.populationVariance(deltas));
	}

}
