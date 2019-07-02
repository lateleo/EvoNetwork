package genetics;

import java.util.List;

import utils.RNG;

/* 
 * Abstract parent class for all the different types of Genes.
 * Also contains code for the internal functional interface "Mutation".
*/
public abstract class Gene {
	
	private static double mutationRate = 0.01;
	private static double invMutationRate = 1/mutationRate;

	protected abstract Gene clone();

	public abstract Gene mutate();
	
	protected Gene mutate(List<Mutation> mutations) {
		double rand = RNG.getDouble();
		if (rand > mutationRate) return this;
		double bigRand = rand*invMutationRate;
		int index = (int) bigRand*mutations.size();
		Gene mutant = clone();
		mutations.get(index).getMutant(mutant);
		return mutant;
	}
	
	protected interface Mutation {
		public void getMutant(Gene mutant);
	}
}
