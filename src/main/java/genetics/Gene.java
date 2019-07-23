package genetics;

import java.util.List;

import ecology.Species;
import utils.RNG;

/* 
 * Abstract parent class for all the different types of Genes.
 * Also contains code for the internal functional interface "Mutation".
*/
public abstract class Gene {
	private static double mutationRate = Species.mutationRate;
	private static double invMutationRate; // used to speed up computation

	protected abstract Gene clone();

	public abstract Gene mutate(double rand);
		
	protected Gene mutate(Mutation[] mutations, double rand) {
		int index = (int) rand*mutations.length;
		Gene mutant = clone();
		mutations[index].getMutant(mutant);
		return mutant;
	}
	
	protected interface Mutation {
		public void getMutant(Gene mutant);
	}
}
