package network;


import org.apache.commons.math3.stat.regression.SimpleRegression;

import genetics.Genome;


public class Organism {
	private double performance;
	private double regressionPerf;
	private double fitness;
	public int age = 0;
	private Genome genome;
	private NeuralNetwork network;
	
	public Organism(Genome genome) {
		this.genome = genome;
	}
	
	public Organism(Organism a, Organism b, boolean forcedMutation) {
		this.genome = new Genome(a.genome.getHaploidSet(forcedMutation), b.genome.getHaploidSet(forcedMutation));
	}
	
	public void updatePerfAndAge(double sizeRMS) {
		age++;
		performance = network.getAccuracy()*Math.sqrt(sizeRMS/network.size());
		regressionPerf = 1/(1 - performance) - 1;
	}
	
	public double getPerformance() {
		return performance;
	}
	
	public double getRegressionPerf() {
		return regressionPerf;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setFitness(SimpleRegression regression) {
		fitness = regressionPerf - regression.predict(age);
	}
	
	public double getFitness() {
		return fitness;
	}
	
	
	public NeuralNetwork getNetwork() {
		return network;
	}
	
	public Genome genome() {
		return genome;
	}
	
	public long genomeSize() {
		return genome.size();
	}
	
	public int networkSize() {
		return network.size();
	}
	
	public void run() {
		network.run();
	}
	
	public void buildNetwork() {
		network = new NeuralNetwork(this);
	}

}
