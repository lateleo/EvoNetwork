package genetics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import utils.ConnTuple;

//	Used for Orphan removal and but created/added to in earlier steps for efficiency
public class NodePhene {
	private Double bias = null;
	private Double xprSum = null;	
	List<double[]> triplets = new ArrayList<>();
	
	public Set<ConnTuple> upConns = new HashSet<>();
	public Set<ConnTuple> downConns = new HashSet<>();
	
	public void addGene(NodeGene gene) {
		triplets.add(new double[] {gene.xprLevel, gene.learnFactor, gene.bias});
	}

	public void addUp(ConnTuple tuple) {
		upConns.add(tuple);
	}

	public void addDown(ConnTuple tuple) {
		downConns.add(tuple);
	}
	
	public double getXprSum() {
		if (xprSum == null) {
			xprSum = 0.0;
			for (double[] triplet : triplets) {
				xprSum += triplet[0];
			}			
		}
		return xprSum;
	}
	
	public double getLearnRate() {
		double learnRate = 0;
		if (getXprSum() != 0) {
			double sum = 0;
			for (double[] triplet : triplets) {
				sum += triplet[0]*triplet[1];
			}
			learnRate = sum/xprSum;
		}
		return 1/(1 + Math.pow(Math.E, -learnRate));
	}
	
	public double getAvgBias() {
		double sum = 0;
		for (double[] triplet : triplets) {
			sum += triplet[0]*triplet[2];
		}
		return sum/xprSum;
	}
	
	public double getBias() {
		return bias;
	}
	
	public void setBias() {
		if (bias == null) bias = getAvgBias();
	}

}
