package ecology;

import java.util.ArrayList;
import java.util.List;

import network.Organism;
import utils.RNG;
import utils.Stats;

public class Population {
	private static int populationSize = Species.populationSize;
	private static int simulatedGenerations = Species.simulatedGenerations;
	
	List<Organism> adults = new ArrayList<Organism>();
	List<Organism> youth = new ArrayList<Organism>();
	
	public Population(List<Organism> orgs) {
		adults = orgs;
	}
	
	public void simulateGenerations() {
		for (int gen = 0; gen < simulatedGenerations; gen++) {
			while (adults.size() > populationSize/2) {
				adults.remove(RNG.getIntMax(adults.size()));
			}
			while (adults.size() + youth.size() < populationSize) {
				int a = RNG.getIntMax(adults.size());
				int b;
				do {
					b = RNG.getIntMax(adults.size());
				} while (b == a);
				youth.add(new Organism(adults.get(a), adults.get(b)));
			}
			adults.addAll(youth);
			youth.clear();
		}
		youth.addAll(adults);
		adults.clear();
	}
	
	public void iterate(double target) {
		int gen = 1;
		double meanAccuracy = 0.0;
		while (meanAccuracy < target) {
			runGeneration();
			adults.addAll(youth);
			youth.clear();
			adults.forEach(org -> System.out.println(org.getNetwork().getAccuracy()));
			System.out.println("Gen " + gen + ", Mean: " + meanAccuracy);
			if (meanAccuracy < target) getNextGeneration();
			gen++;
		}
	}
	
	private void getNextGeneration() {
		updateFitness();
		sortByFitness();
		filter();
		repopulate();
	}
	
	private void runGeneration() {
		youth.forEach(org -> org.buildNetwork());
		youth.forEach(org -> org.getNetwork().run());
	}
	
	private void updateFitness() {
		double mean = adults.stream().mapToInt(a -> a.size()).average().getAsDouble();
		adults.forEach(a -> a.setFitness(mean));
	}
	
	private void sortByFitness() {
		adults.sort((a,b)-> {
			double delta = b.getFitness() - a.getFitness();
			return (int)(Math.signum(delta)*Math.ceil(Math.abs(delta)));
		});
	}
	
	private void filter() {
		List<Organism> survivors = new ArrayList<Organism>();
		int size = adults.size();
		int i = 0;
		for (Organism adult : adults) {
			if (RNG.getIntMax(size) >= i) survivors.add(adult);
			i++;
		}
		System.out.println(adults.size() - survivors.size() + " Organisms removed.");
		adults.retainAll(survivors);
	}

	private void repopulate() {
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
