package network;

import java.util.Hashtable;
import java.util.Map;

import ecology.Species;
import utils.ConnTuple;

public class TopLayer extends Layer {
	private static int nodeCount = Species.topNodes;
	double[] outputs = new double[nodeCount];
	
	public TopLayer(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights, NeuralNetwork network) {
		super(nodeBiases, inputConnWeights, network);
	}
	
	protected void fillNodes(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights) {
		if (nodeBiases == null) nodeBiases = new Hashtable<>();
		for (int i = 0; i < nodeCount; i++) {
			if (!nodeBiases.containsKey(i)) {
				nodeBiases.put(i, 0.0);
			}
		}
		super.fillNodes(nodeBiases, inputConnWeights);
	}
	
	@Override
	public void run() {
		super.run();
		double sum = nodes.values().stream().mapToDouble(d->d.output).sum();
		nodes.forEach((nodeNum,node)-> outputs[nodeNum] = node.output/sum);
	}
	
	@Override
	public void backProp(float learning_rate) {
		nodes.forEach((nodeNum,node) -> node.backProp(learning_rate));
	}

}
