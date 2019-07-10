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
	private NeuralNetwork network;
	
	public Organism(Genome genome) {
		this.genome = genome;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public Genome getGenome() {
		return genome;
	}
	
	public NeuralNetwork getNetwork() {
		return network;
	}
	
	public void buildNetwork() {
		Transcriptome xscript = genome.trasncribe();
		Map<Integer,Map<Integer,Double>> laysAndNodes = xscript.getLaysAndNodes();
		Map<ConnTuple,Double> connWeights = xscript.getConnWeights();
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
		network.setTop(new TopLayer(laysAndNodes.get(-1), topConns, network));
	}
	

}
