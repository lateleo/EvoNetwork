package genetics;

import java.util.HashSet;
import java.util.Set;

import staticUtils.MathUtils;
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
	
	public double getLearnRate() {
		if (learnRate == null) learnRate = MathUtils.sigmoid(getAvg(1));	
		return learnRate;
	}
	
	public double getBias() {
		if (bias == null) bias = getAvg(2);
		return bias;
	}
	
	public void addUp(ConnTuple tuple) {
		upConns.add(tuple);
	}

	public void addDown(ConnTuple tuple) {
		downConns.add(tuple);
	}
	
	public boolean isOrphan(Set<ConnTuple> connTuples, boolean direction) {
		boolean orphan = filterConns(connTuples, direction);
		if (orphan) connTuples.removeAll((direction)? upConns : downConns);
		return orphan;
	}
	
	public boolean filterConns(Set<ConnTuple> connTuples, boolean direction) {
		Set<ConnTuple> connSet = (direction)? downConns : upConns;
		connSet.retainAll(connTuples);
		return connSet.isEmpty();
	}

}
