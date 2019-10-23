package genetics;

import java.util.HashSet;
import java.util.Set;

import utils.ConnTuple;

//	Used for Orphan removal and but created/added to in earlier steps for efficiency
public class NodePhene extends Phenotype {
	private Double bias = null;
	private Double learnRate = null;
	
	public Set<ConnTuple> upConns = new HashSet<>();
	public Set<ConnTuple> downConns = new HashSet<>();
	
	public void addGene(NodeGene gene) {
		vals.add(new double[] {gene.xprLevel, gene.learnFactor, gene.bias});
	}

	public void addUp(ConnTuple tuple) {
		upConns.add(tuple);
	}

	public void addDown(ConnTuple tuple) {
		downConns.add(tuple);
	}
	
	public double getLearnRate() {
		if (learnRate == null) learnRate = 1/(1 + Math.pow(Math.E, -getAvg(1)));	
		return learnRate;
	}
	
	public double getBias() {
		if (bias == null) bias = getAvg(2);
		return bias;
	}

}
