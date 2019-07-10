package network;

import java.util.Map;
import java.util.TreeMap;

import ecology.Species;
import utils.ConnTuple;

public class TopLayer extends Layer {
	private static int nodeCount = Species.topNodes;
	Map<Integer,Double> squishedOutputs = new TreeMap<Integer,Double>();
	
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
		Map<Integer,Double> outputs = new TreeMap<Integer,Double>();
		nodes.forEach((nodeNum,node) -> outputs.put(nodeNum, node.getOutput()));
		
	}

}
