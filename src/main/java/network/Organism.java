package network;

import genetics.Genome;
import genetics.HaploidSet;


public class Organism {
	private double performance;
	private double regressionPerf;
	private double fitness = 0;
	public int age = 0;
	private Genome genome;
	private NeuralNetwork network;
	
	private double mRate, invMRate, mMag, slip;
	private boolean readyForHaploid = false;
	
	public Organism(Genome genome) {
		this.genome = genome;
	}
	
	public Organism(Organism a, Organism b, boolean forcedMutation) {
		this.genome = new Genome(a.getHaploidSet(forcedMutation), b.getHaploidSet(forcedMutation), this);
	}
	
	private HaploidSet getHaploidSet(boolean forcedMutation) {
		if (!readyForHaploid) {
			double[] vals = genome.transcriptome().transcribeRegGenes();
			mRate = vals[0];
			invMRate = 1/mRate;
			mMag = vals[1];
			slip = vals[2];
		}
		return genome.getHaploidSet(forcedMutation);
	}
	
	
	public void buildNetwork() {
		network = new NeuralNetwork(this);
	}
	
	public void updatePerformance() {
		age++;
		performance = network.getAccuracy();
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
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
		
	public double getFitness() {
		return fitness;
	}
	
	public double getMRate() {
		return mRate;
	}
	
	public double getInvMRate() {
		return invMRate;
	}
	
	public double getMMag() {
		return mMag;
	}
	
	public double getSlip() {
		return slip;
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
	
	public void learn() {
		network.backProp();
	}

}
