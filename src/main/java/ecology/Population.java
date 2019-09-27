package ecology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import network.BottomLayer;
import network.NeuralNetwork;
import network.Organism;
import utils.RNG;
import utils.Stats;

public class Population {
	private static int populationSize = Species.populationSize;
	private static int simulatedGenerations = Species.simulatedGenerations;
	
	private static Population instance = null;

	List<Organism> adults = new ArrayList<Organism>();
	List<Organism> youth = new ArrayList<Organism>();
	
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

	public void iterate(double target) {
		int gen = 1;
		double meanAccuracy = 0.0;
		while (meanAccuracy < target) {
			runGeneration();
			meanAccuracy = getMeanAccuracy();
			int maxAge = 0;
			for (Organism org : adults) if (org.age > maxAge) maxAge = org.age;
			System.out.println("Gen " + gen + ": Max Age: " + maxAge + ", Accuracy: " + meanAccuracy);
			if (meanAccuracy < target) {
				getNextGeneration();
				gen++;				
			}
		}
	}
	
	public void testGeneration() {
		NeuralNetwork.testBatch();
		adults.forEach(org -> org.run());
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
		List<Organism> orgs = new ArrayList<>(adults);
		NeuralNetwork.nextBatch();
		System.out.println("Running Batch...");
		orgs.forEach(org -> org.run());
	}

	public void getNextGeneration() {
		updateFitness();
		sortByFitness();
		filter();
		repopulate(false);
	}

	public void updateFitness() {
		double sizeRMS = 0.0;
		for (Organism org : adults) sizeRMS += Math.pow(org.networkSize(),2);
		sizeRMS = Math.sqrt(sizeRMS/adults.size());
		for (Organism org : adults) org.updatePerfAndAge(sizeRMS);
		SimpleRegression regression = Stats.getRegression(adults);
		for (Organism org : adults) org.setFitness(regression);
	}

	public void sortByFitness() {
		adults.sort((a, b) -> {
			double delta = b.getPerformance() - a.getPerformance();
			return (int) (Math.signum(delta) * Math.ceil(Math.abs(delta)));
		});
	}

	public void filter() {
		Organism best = null;
		for (Organism org : adults) {
			if (best == null || org.getFitness() > best.getFitness()) best = org;
		}
		double[] stats = getFitnessStats();
		List<Organism> lineUp = new ArrayList<>(adults);
		List<Organism> survivors = new ArrayList<>();
		while (adults.size() > populationSize/2) {
			Collections.shuffle(lineUp);
			for (Organism org : lineUp) {
				if (adults.size() <= populationSize/2) break;
				if (org.equals(best)) survivors.add(org);
				else if (RNG.getGauss(stats[0], stats[1]) <= org.getFitness()) survivors.add(org);
				else adults.remove(org);
			}
			lineUp.clear();
			lineUp.addAll(survivors);
			survivors.clear();
		}
	}

	public void repopulate(boolean forcedMutation) {
		double[] stats = getFitnessStats();
		while (adults.size() + youth.size() < populationSize) {
			Organism org1 = null;
			Collections.shuffle(adults);
			for (Organism org2 : adults) {
				if (adults.size() + youth.size() >= populationSize) break;
				if (org2.equals(org1)) break;
				if (forcedMutation || RNG.getGauss(stats[0], stats[1]) <= org2.getFitness()) {
					if (org1 == null) org1 = org2;
					else {
						youth.add(new Organism(org1, org2, forcedMutation));
						org1 = null;
					}
				}
			}
		}
	}
	
	public double[] getFitnessStats() {
		double mean = 0.0;
		for (Organism org : adults) mean += org.getFitness();
		mean /= adults.size();
		double sigma = Stats.getFitnessSigma(adults);
		double[] stats = {sigma,mean};
		return stats;
	}
	
	public void remove(Organism org) {
		adults.remove(org);
	}

}
