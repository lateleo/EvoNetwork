package genetics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

public class ConnPhene {
	private Double xprSum = null;
	List<Pair<Double, Double>> valPairs = new ArrayList<Pair<Double,Double>>();

	public void addGene(ConnGene gene) {
		valPairs.add(new Pair<Double,Double>(gene.xprLevel, gene.weight));
	}

	public double getXprSum() {
		if (xprSum == null) {
			xprSum = 0.0;
			for (Pair<Double,Double> pair : valPairs) {
				xprSum += pair.getFirst();
			}				
		}
		return xprSum;
	}
	
	public double getAvgWeight() {
		double sum = 0;
		for (Pair<Double,Double> pair : valPairs) {
			sum += pair.getFirst()*pair.getSecond();
		}
		return sum/xprSum;
	}
}
