package genetics;

import org.apache.commons.math3.util.Pair;

public class ConnPhene extends Phenotype {

	public void addGene(ConnGene gene) {
		valPairs.add(new Pair<Double,Double>(gene.xprLevel, gene.weight));
	}
}
