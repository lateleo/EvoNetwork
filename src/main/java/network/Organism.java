package network;

import java.util.Map;
import java.util.TreeMap;

import genetics.Genome;
import genetics.Transcriptome;
import utils.CMUtils;
import utils.ConnSetPair;
import utils.ConnTuple;

public class Organism {
	private double fitness;
	private Genome genome;
	private int size;
	private NeuralNetwork network;
	
	public Organism(Genome genome) {
		this.genome = genome;
		size = genome.size();
	}
	
	public Organism(Organism a, Organism b, boolean forcedMutation) {
		this.genome = new Genome(a.genome.getHaploidSet(forcedMutation), b.genome.getHaploidSet(forcedMutation));
		size = genome.size();
	}
	
	public double getFitness() {
		return fitness;
	}
		
	public void setFitness(double meanSize) {
		fitness = network.getAccuracy()*meanSize/genome.size();
	}
	
	public NeuralNetwork getNetwork() {
		return network;
	}
	
	public int size() {
		return size;
	}
	
	public void buildNetwork() {
		Transcriptome xscript = genome.transcribe();
		network = new NeuralNetwork(xscript);
	}
	

}
