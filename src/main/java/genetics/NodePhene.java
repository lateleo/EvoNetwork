package genetics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import utils.ConnTuple;

//	Used for Orphan removal and but created/added to in earlier steps for efficiency
public class NodePhene extends Phenotype {
	private Double bias = null;

	
	public Set<ConnTuple> upConns = new HashSet<>();
	public Set<ConnTuple> downConns = new HashSet<>();
	
	public void addGene(NodeGene gene) {
		valPairs.add(new Pair<Double,Double>(gene.xprLevel, gene.bias));
	}
	
	public double getBias() {
		return bias;
	}
	
	public void setBias() {
		if (bias == null) bias = getWeightedAvg();
	}

	public void addUp(ConnTuple tuple) {
		upConns.add(tuple);
	}

	public void addDown(ConnTuple tuple) {
		downConns.add(tuple);
	}
}
