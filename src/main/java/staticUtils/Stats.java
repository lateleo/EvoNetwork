package staticUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import network.Node;
import network.Organism;

public class Stats {
	
	public static double meanOutput(Collection<Node> vals) {
		double[] outputs = vals.stream().mapToDouble(node -> node.output).toArray();
		return StatUtils.mean(outputs);
	}
	
	public static double nodeSigma(Collection<Node> vals) {
		double[] outputs = vals.stream().mapToDouble(node -> node.output).toArray();
		return Math.sqrt(StatUtils.populationVariance(outputs));
	}
	
	public static SimpleRegression getRegression(List<Organism> orgs) {
		List<double[]> data = new ArrayList<double[]>();
		for (Organism org : orgs) {
			double[] pair = new double[2];
			pair[0] = org.getAge();
			pair[1] = org.getRegressionPerf();
			data.add(pair);
		}
		double[][] dataArray = data.toArray(new double[0][0]);
		SimpleRegression regression = new SimpleRegression();
		regression.addData(dataArray);
		return regression;
	}
	
	public static double getFitnessSigma(List<Organism> orgs, ToDoubleFunction<Organism> function) {
		double[] deltas = orgs.stream().mapToDouble(function).toArray();
		return Math.sqrt(StatUtils.populationVariance(deltas));
	}

}
