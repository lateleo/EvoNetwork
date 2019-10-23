package staticUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import network.Organism;

public class Stats {
	
	public static <T> double getMean(Collection<T> inputs, ToDoubleFunction<T> func) {
		double sum = 0;
		for (T input : inputs) sum += func.applyAsDouble(input);
		return sum/inputs.size();
	}
	
	public static <T> double getSigma(Collection<T> inputs, ToDoubleFunction<T> func, double mean) {
		double squrSum = 0;
		for (T input : inputs) {
			double val = func.applyAsDouble(input);
			squrSum += val*val;
		}
		return Math.sqrt((squrSum/inputs.size()) - mean*mean);
	}
	
	public static <T> double getMaxDouble(Collection<T> inputs, ToDoubleFunction<T> func) {
		double max = Double.NEGATIVE_INFINITY;
		for (T input : inputs) {
			double val = func.applyAsDouble(input);
			max = Double.max(max, val);
		}
		return max;
	}
	
	public static <T> int getMaxInt(Collection<T> inputs, ToIntFunction<T> func) {
		int max = Integer.MIN_VALUE;
		for (T input : inputs) {
			int val = func.applyAsInt(input);
			max = Integer.max(max, val);
		}
		return max;
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
	
//	public static double getFitnessSigma(List<Organism> orgs, ToDoubleFunction<Organism> function) {
//		double[] vals = orgs.stream().mapToDouble(function).toArray();
//		return Math.sqrt(StatUtils.populationVariance(vals));
//	}

}
