package genetics;

import org.apache.commons.math3.util.Pair;

public class ConnPhene extends Phenotype {
	private Double weight = null; 

	public void addGene(ConnGene gene) {
		valPairs.add(new Pair<Double,Double>(gene.xprLevel, gene.weight));
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight() {
		if (weight == null) weight = getWeightedAvg();
	}
}
