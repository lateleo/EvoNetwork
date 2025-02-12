package genetics;

import ecology.Species;
import staticUtils.RNG;

/* 
 * Abstract parent class for all the different types of Genes.
 * Also contains code for the internal functional interface "Mutation".
*/
public abstract class Gene {
	
	public double activation, xprLevel;


	protected void mutateXpr(double mag) {
		xprLevel += RNG.getPseudoGauss(mag);
	}
	
	protected void mutateActivation(double mag) {
		activation += RNG.getPseudoGauss(mag);
	}
	
	/*
	 * Abstract method, defined in child classes, used to create an exact copy of a gene, to later be mutated.
	 */
	protected abstract Gene clone();

	/*
	 * This is the method that is called by the Chromosome class in it's "mutateAll" method. it is abstract
	 * because its functionality varies slightly between the child classes, because of the difference in
	 * possible mutations that could occur.
	 */
	public abstract Gene mutate(double rand, double mag);
	
	
	/*
	 * This method is only called by the child class overrides of the above public method, and is where
	 * the actual mutation process happens. it takes in a list of mutations, given by the child class,
	 * and chooses one to execute based on the randomly generated parameter 'rand'.
	 */
	protected Gene mutate(Mutation[] mutations, double rand, double mag) {
		int index = (int) rand*mutations.length;
		Gene mutant = clone();
		mutations[index].getMutant(mutant, mag);
		return mutant;
	}
	
	protected interface Mutation {
		public void getMutant(Gene mutant, double mag);
	}
}
