package genetics;


public class ConnPhene extends Phenotype {
	private Double weight = null; 

	public void addGene(ConnGene gene) {
		vals.add(new double[] {gene.xprLevel, gene.weight});
	}

	
	public double getWeight() {
		if (weight == null) weight = getAvg(1);
		return weight;
	}
}
