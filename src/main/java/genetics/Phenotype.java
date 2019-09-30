package genetics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

public abstract class Phenotype {
	private Double xprSum = null;
	List<Pair<Double, Double>> valPairs = new ArrayList<Pair<Double,Double>>();
	
	public double getXprSum() {
		if (xprSum == null) {
			xprSum = 0.0;
			for (Pair<Double,Double> pair : valPairs) {
				xprSum += pair.getFirst();
			}
			xprSum /= valPairs.size();				
		}
		return xprSum;
	}
	
	public double getWeightedAvg() {
		return valPairs.stream().mapToDouble((pair) -> Math.abs(pair.getFirst())*pair.getSecond()).sum()/xprSum;
	}

}
