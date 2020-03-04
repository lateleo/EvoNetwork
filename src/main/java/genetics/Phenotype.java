package genetics;

import java.util.ArrayList;
import java.util.List;

public abstract class Phenotype {
	private boolean summed = false;
	private double xprSum;
	private double absXprSum;
	List<double[]> vals = new ArrayList<double[]>();
	
	public double getXprSum() {
		calcXpr();
		return xprSum;
	}
	
	public double getAbsXprSum() {
		calcXpr();
		return absXprSum;
	}
	
	
	private void calcXpr() {
		if (!summed) {
			for (double[] pair : vals) {
				xprSum += pair[0];
				absXprSum += Math.abs(pair[0]);
			}				
		}
	}
	
	protected double getAvg(int index) {
		double sum = 0;
		for (double[] pair : vals) {
			sum += pair[0]*pair[index];
		}
		return sum/getAbsXprSum();
	}

}
