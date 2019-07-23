package genetics;

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
	public Gene mutate(double rand) {
		return this;
	}

}
