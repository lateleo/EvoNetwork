package network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ecology.Species;
import utils.ConnTuple;

public class TopLayer extends Layer {
	private static int nodeCount = Species.topNodes;
	double[] outputs = new double[nodeCount];
	
	public TopLayer(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights, NeuralNetwork network) {
		super(nodeBiases, inputConnWeights, network);
	}
	
	protected void fillNodes(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights) {
		for (int i = 0; i < nodeCount; i++) if (!nodeBiases.containsKey(i)) nodeBiases.put(i, 0.0);
		super.fillNodes(nodeBiases, inputConnWeights);
	}
	
	@Override
	public void run() {
		super.run();
		double sum = nodes.values().stream().mapToDouble(d->d.output).sum();
		nodes.forEach((nodeNum,node)-> outputs[nodeNum] = node.output/sum);
	}

}
