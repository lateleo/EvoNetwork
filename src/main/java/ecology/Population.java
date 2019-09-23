package ecology;

import java.util.ArrayList;
import java.util.List;

import network.BottomLayer;
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
//			System.out.println("Gen " + gen + "...");
			while (adults.size() > populationSize / 2) {
				adults.remove(RNG.getIntMax(adults.size()));
			}
			repopulate(true);
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
			meanAccuracy = getMeanAccuracy();

			System.out.println("Gen " + gen + ": " + meanAccuracy);
			if (meanAccuracy < target) {
				getNextGeneration();
				gen++;				
			}

		}

	}
	
	public void testGeneration() {
		BottomLayer.testBatch();
		adults.forEach(org -> org.getNetwork().run());
		getMeanAccuracy();
	}
	
	public void buildNetworks() {
//		System.out.println("Building Networks...");
		youth.forEach(org -> org.buildNetwork());
	}
	
	public double getMeanAccuracy() {
		double meanAccuracy = 0.0;
		for (Organism org : adults) {
			double accuracy = org.getNetwork().getAccuracy();
			meanAccuracy += accuracy;
		}
		meanAccuracy /= populationSize;
		return meanAccuracy;
	}

	public void runGeneration() {
		buildNetworks();
		adults.addAll(youth);
		youth.clear();
		BottomLayer.nextBatch();
//		System.out.println("Running Batch...");
		adults.forEach(org -> org.getNetwork().run());
	}

	public void getNextGeneration() {
		updateFitness();
		sortByFitness();
		filter();
		repopulate(false);
	}

	public void updateFitness() {
		double mean = adults.stream().mapToInt(a -> a.size()).average().getAsDouble();
		adults.forEach(a -> a.setFitness(mean));
	}

	public void sortByFitness() {
		adults.sort((a, b) -> {
			double delta = b.getFitness() - a.getFitness();
			return (int) (Math.signum(delta) * Math.ceil(Math.abs(delta)));
		});
	}

	public void filter() {
		List<Organism> survivors = new ArrayList<Organism>(adults);
		while (survivors.size() > adults.size()/2) {
			int a = RNG.getIntMax(survivors.size());
			int b = RNG.getIntMax(survivors.size());
			if (b < a) survivors.remove(a);
		}
		adults.retainAll(survivors);
	}

	public void repopulate(boolean forcedMutation) {
		while (adults.size() + youth.size() < populationSize) {
			int a = RNG.getIntMax(adults.size());
			int b;
			do {
				b = RNG.getIntMax(adults.size());
			} while (b == a);
			Organism newborn = new Organism(adults.get(a), adults.get(b), forcedMutation);
//			System.out.println(newborn.size());
			youth.add(newborn);
		}
	}

}
