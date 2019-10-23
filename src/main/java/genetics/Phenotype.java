package genetics;

import java.util.ArrayList;
import java.util.List;

public abstract class Phenotype {
	private Double xprSum = null;
	List<double[]> vals = new ArrayList<double[]>();
	
	public double getXprSum() {
		if (xprSum == null) {
			xprSum = 0.0;
			for (double[] pair : vals) {
				xprSum += pair[0];
			}				
		}
		return xprSum;
	}
	
	protected double getAvg(int index) {
		double sum = 0;
		for (double[] pair : vals) {
			sum += pair[0]*pair[index];
		}
		return sum/getXprSum();
	}

}
