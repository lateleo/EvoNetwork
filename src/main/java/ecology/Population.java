package ecology;

import java.util.ArrayList;
import java.util.List;

import network.Organism;
import utils.RNG;

public class Population {
	private static int populationSize = Species.populationSize;
	
	List<Organism> adults = new ArrayList<Organism>();
	List<Organism> youth = new ArrayList<Organism>();
	
	public void runGeneration() {
		youth.forEach(org -> org.getNetwork().run());
	}
	
	public void getNextGeneration() {
		adults.addAll(youth);
		youth.clear();
		updateFitness();
		sortByFitness();
		filter();
		repopulate();
	}
	
	public void updateFitness() {
		double mean = adults.stream().mapToInt(a -> a.size()).average().getAsDouble();
		adults.forEach(a -> a.setFitness(mean));
	}
	
	public void sortByFitness() {
		adults.sort((a,b)-> {
			double delta = b.getFitness() - a.getFitness();
			return (int)(Math.signum(delta)*Math.ceil(Math.abs(delta)));
		});
	}
	
	public void filter() {
		List<Organism> survivors = new ArrayList<Organism>();
		int size = adults.size();
		int i = 0;
		for (Organism adult : adults) {
			if (RNG.getIntMax(size) >= i) survivors.add(adult);
			i++;
		}
		adults.retainAll(survivors);
	}

	public void repopulate() {
		while (adults.size() + youth.size() < populationSize) {
			int a = RNG.getIntLowBias(adults.size());
			int b;
			do {
				b = RNG.getIntLowBias(adults.size());
			} while (b == a);
			youth.add(new Organism(adults.get(a), adults.get(b)));
		}
	}

}
