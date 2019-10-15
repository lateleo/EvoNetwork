package ecology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import network.NeuralNetwork;
import network.Organism;
import staticUtils.RNG;
import staticUtils.Stats;

public class Population {
	private static int populationSize = Species.populationSize;
	private static double invPopSize = 1.0/populationSize;
	private static int simulatedGenerations = Species.simulatedGenerations;
	
	private static Population instance = null;

	List<Organism> adults = new ArrayList<Organism>();
	List<Organism> youth = new ArrayList<Organism>();
	
	private double meanAccuracy = 0;
	private int maxAge = 0;
	
	public static Population getInstance() {
		return instance;
	}
	
	public static Population setInstance(List<Organism> orgs) {
		if (instance == null) instance = new Population(orgs);
		return instance;
	}

	private Population(List<Organism> orgs) {
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
	
	public void buildNetworks() {
//		System.out.println("Building Networks...");
		youth.forEach(org -> org.buildNetwork());
	}
	
	public void testGeneration() {
		NeuralNetwork.testBatch();
		adults.forEach(org -> org.run());
		getMeanAccuracy();
	}

	public void iterate(double target) {
		int gen = 1;
		while (meanAccuracy < target) {
			runGeneration();
			getMeanAccuracy();
			getMaxAge();
//			System.out.println("Gen " + gen + ": Max Age: " + maxAge + ", Accuracy: " + meanAccuracy);
			System.out.println(meanAccuracy);

			if (meanAccuracy < target) {
				getNextGeneration();
				gen++;				
			}
		}
	}
	
	public void getMeanAccuracy() {
		meanAccuracy = 0.0;
		for (Organism org : adults) {
			double accuracy = org.getNetwork().getAccuracy();
			meanAccuracy += accuracy;
		}
		meanAccuracy *= invPopSize;
	}
	
	public void getMaxAge() {
		maxAge = 0;
		for (Organism org : adults) if (org.age > maxAge) maxAge = org.age;
	}

	public void runGeneration() {
		buildNetworks();
		adults.addAll(youth);
		youth.clear();
		List<Organism> orgs = new ArrayList<>(adults);
		NeuralNetwork.nextBatch();
//		System.out.println("Running Batch...");
		orgs.forEach(org -> org.run());
	}

	public void getNextGeneration() {
		updateFitness();
		sortByFitness();
		filter();
		learn();
		repopulate(false);
	}

	public void updateFitness() {
		if (meanAccuracy >= 0.5) System.out.println("Updating Fitness...");
		if (maxAge > 0) {
			for (Organism org : adults) org.updatePerf();
			SimpleRegression regression = Stats.getRegression(adults);
			for (Organism org : adults) org.setFitness(regression);
		} else {
			double meanPerf = 0;
			for (Organism org : adults) {
				org.updatePerf();
				meanPerf += org.getRegressionPerf();
			}
			meanPerf *= invPopSize;
			for (Organism org : adults) org.setFitness(meanPerf);
		}
		
	}

	public void sortByFitness() {
		if (meanAccuracy >= 0.5) System.out.println("Sorting by Fitness...");
		adults.sort((a, b) -> {
			double delta = b.getFitness() - a.getFitness();
			return (int) (Math.signum(delta) * Math.ceil(Math.abs(delta)));
		});
	}

	public void filter() {
		if (meanAccuracy >= 0.5) System.out.println("Filtering...");
		Organism best = null;
		for (Organism org : adults) {
			if (best == null || org.getFitness() > best.getFitness()) best = org;
		}
//		if (meanAccuracy >= 0.5) System.out.println("Best Chosen...");
		double[] stats = getMeanAndSigma((org) -> org.getFitness());
//		if (meanAccuracy >= 0.5) System.out.println("Mean and Sigma...");
		List<Organism> lineUp = new ArrayList<>(adults);
		List<Organism> survivors = new ArrayList<>();
		while (adults.size() > populationSize/2) {
			Collections.shuffle(lineUp);
//			if (meanAccuracy >= 0.5) System.out.println("Shuffle...");
			for (Organism org : lineUp) {
				if (adults.size() <= populationSize/2) break;
				double rng = RNG.getGauss(stats[1], stats[0]);
//				if (meanAccuracy >= 0.5) System.out.println("RNG: " + rng);
				if (org.equals(best)) {
//					if (meanAccuracy >= 0.5) System.out.println("Best Org...");
					survivors.add(org);
				}
				else if (rng <= org.getFitness()) {
//					if (meanAccuracy >= 0.5) System.out.println("Survivor: " + org.getFitness());
					survivors.add(org);
				}
				else {
//					if (meanAccuracy >= 0.5) System.out.println("Culled...");
					adults.remove(org);
				}

			}
			lineUp.clear();
			lineUp.addAll(survivors);
			survivors.clear();
		}
	}
	
	public void learn() {
//		if (meanAccuracy >= 0.5) System.out.println("Learning...");
		for (Organism org : adults) org.learn();
	}

	public void repopulate(boolean forced) {
		double scalar = 0;
		if (!forced) {
//			if (meanAccuracy >= 0.5) System.out.println("Repopulating...");
			double[] stats = getMeanAndSigma((org)-> org.networkSize());
//			scalar = Math.max(0.0, 2*(meanAccuracy - 0.5));
//			if (meanAccuracy >= 0.5) System.out.println("Scalar: " + scalar);
			for (Organism org : adults) org.setAttractiveness(stats[0], stats[1]);
		}
		while (adults.size() + youth.size() < populationSize) {
			Organism org1 = null;
			Collections.shuffle(adults);
			for (Organism org2 : adults) {
				if (adults.size() + youth.size() >= populationSize) break;
				if (org2.equals(org1)) break;
				double rng = RNG.getGauss();
				if (meanAccuracy >= 0.5) {
//					System.out.println("RNG: " + rng + "; Attractiveness: " + org2.getAttractiveness());
				}
				if (forced || rng <= scalar*org2.getAttractiveness()) {
					if (org1 == null) org1 = org2;
					else {
						youth.add(new Organism(org1, org2, forced));
						org1 = null;
					}
				}
			}
		}
	}
	
	public double[] getMeanAndSigma(ToDoubleFunction<Organism> function) {
		double mean = 0.0;
		for (Organism org : adults) mean += function.applyAsDouble(org);
		mean /= adults.size();
		double sigma = Stats.getFitnessSigma(adults, function);
		double[] stats = {mean, sigma};
		return stats;
	}
	
	public void remove(Organism org) {
		adults.remove(org);
	}

}
