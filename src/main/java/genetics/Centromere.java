package genetics;

/*
 * A special kind of Gene child class, that simply contains the chromosome number of the chromosome it belongs to.
 * It's clone and mutate methods return itself, because it's sole use is to line up the other genes in homologous chromosomes
 * during recombination.
 */
public class Centromere extends Gene {
	private int chromosomeNum;
	
	Centromere(int chromosomeNum) {
		this.chromosomeNum = chromosomeNum;
	}
	
	public int getChromosomeNum() {
		return chromosomeNum;
	}
	
	@Override
	protected Gene clone() {
		return this;
	}

	@Override
	public Gene mutate(double rand, double mag) {
		return this;
	}

}
