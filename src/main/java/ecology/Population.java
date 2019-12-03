package ecology;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.ToDoubleFunction;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import network.NeuralNetwork;
import network.Organism;
import staticUtils.RNG;
import staticUtils.Stats;

public class Population {
	private static int populationSize = Species.populationSize;
	private static int simulatedGenerations = Species.simulatedGenerations;
	
	private static Population instance = null;

	ArrayList<Organism> adults;
	List<Organism> youth = new ArrayList<Organism>();
	
	private double meanAccuracy = 0;
	private int maxAge = 0;
	private SimpleRegression regression;
	
	public static Population getInstance() {
		return instance;
	}
	
	public static Population setInstance(List<Organism> orgs) {
		if (instance == null) instance = new Population(orgs);
		return instance;
	}

	private Population(List<Organism> orgs) {
		adults = new ArrayList<>(orgs);
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
	
	public void testGeneration() {
		NeuralNetwork.testBatch();
		adults.forEach(org -> org.run());
		meanAccuracy = Stats.getMean(adults, org -> org.getNetwork().getAccuracy());
	}

	public void iterate(double target) {
		int gen = 1;
		Long start = System.currentTimeMillis();
		while (meanAccuracy < target) {
			runGeneration();
			meanAccuracy = Stats.getMean(adults, org -> org.getNetwork().getAccuracy());
			maxAge = Stats.getMaxInt(adults, org -> (int) org.getAge());
			if (gen == 1 || (System.currentTimeMillis() - start) >= 1000) {
				System.out.println("Gen " + gen + "\tAccuracy: " + meanAccuracy);
				start = System.currentTimeMillis();
			}
			if (meanAccuracy < target) {
				getNextGeneration();
				gen++;				
			}
		}
	}
	
	public void runGeneration() {
//		System.out.print("Building Networks...");
		youth.forEach(org -> org.buildNetwork());
//		System.out.println("Done");
		adults.addAll(youth);
		youth.clear();
		List<Organism> orgs = new ArrayList<>(adults);
		NeuralNetwork.nextBatch();
//		System.out.print("Running Batch...");
		orgs.forEach(org -> org.run());
//		System.out.println("Done");
	}

	public void getNextGeneration() {
		for (Organism org : adults) org.updatePerformance();
		updateFitness();
		filterOrganisms();
		for (Organism org : adults) org.learn();
		repopulate(false);
	}

	public void updateFitness() {
//		System.out.print("Updating Fitness...");
		if (maxAge > 0) {
			regression = Stats.getRegression(adults);
			ToDoubleFunction<Organism> func = (org) -> {
				return org.getPerformance() - (1 - 1/(1 + regression.predict(org.age)));
			};
			double sigma = Stats.getSigma(adults, func, 0);
			for (Organism org : adults) {
				double delta = func.applyAsDouble(org);
				org.setFitness((delta)/sigma);
			}
		} else {
			double mean = Stats.getMean(adults, org -> org.getPerformance());
			double sigma = Stats.getSigma(adults, org -> org.getPerformance(), mean);
			for (Organism org : adults) {
				org.setFitness((org.getPerformance() - mean)/sigma);
			}
		}

//		System.out.println("Done");
	}

	public void filterOrganisms() {
//		System.out.println("Filtering...");
		adults.sort((a, b) -> {
			double delta = b.getFitness() - a.getFitness();
			return (int) (Math.signum(delta) * Math.ceil(Math.abs(delta)));
		});
		Organism best = adults.get(0);
		while (adults.size()*2 > populationSize) {
			for (ListIterator<Organism> iter = adults.listIterator(); iter.hasNext();) {
				Organism org = iter.next();
				if (adults.size()*2 > populationSize) {
					if (!org.equals(best) && RNG.getGauss() > org.getFitness()) {
						iter.remove();
					}

				}
			}
		}
//		System.out.println("Done");
	}

	
	public void repopulate(boolean forced) {
//		System.out.print("Repopulating...");
		while (adults.size() + youth.size() < populationSize) {
			List<Organism> parents = RNG.getSample(adults, 2);
			youth.add(new Organism(parents.get(0), parents.get(1), forced));
		}
//		System.out.println("Done");
	}
	
	public void remove(Organism org) {
		adults.remove(org);
	}

}
