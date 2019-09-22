package network;

import java.util.Map;
import java.util.TreeMap;

import genetics.Genome;
import genetics.Transcriptome;
import utils.CMUtils;
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
		TreeMap<Integer,TreeMap<Integer,Double>> laysAndNodes = xscript.getLaysAndNodes();
		TreeMap<ConnTuple,Double> connWeights = xscript.getConnWeights();
		network = new NeuralNetwork();
		network.setBottom(new BottomLayer());
		laysAndNodes.forEach((layNum, nodeMap) -> {
			if (layNum != -1) {
				Map<ConnTuple,Double> layConns = CMUtils.getConnsForLayer(connWeights, layNum);
				Layer layer = new Layer(nodeMap, layConns, network);
				network.put(layNum, layer);
			}
		});
		Map<ConnTuple,Double> topConns = CMUtils.getConnsForLayer(connWeights, -1);
		System.out.println("Top Conns: " + topConns.size());
		network.setTop(new TopLayer(laysAndNodes.get(-1), topConns, network));
	}
	

}
