package network;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import utils.CMUtils;
import utils.ConnTuple;
import utils.NodeTuple;
import utils.Stats;

public class Layer extends AbstractLayer {
	Map<NodeTuple, AbstractNode> inputNodes;
	Map<NodeTuple, Double> normInputs;
	
	
	public Layer(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights, NeuralNetwork network) {
		inputNodes = new Hashtable<NodeTuple,AbstractNode>();
		normInputs = new Hashtable<NodeTuple,Double>();
		fillInputs(inputConnWeights, network);
		fillNodes(nodeBiases, inputConnWeights);
	}
	
	protected void fillInputs(Map<ConnTuple,Double> inputConnWeights, NeuralNetwork network) {
		inputConnWeights.keySet().forEach(tuple -> {
			AbstractNode inNode = network.get(tuple.iLay()).get(tuple.iNode());
			inputNodes.put(tuple.getKey(), inNode);
		});
	}
	
	protected void fillNodes(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights) {
		nodeBiases.forEach((nodeNum, bias) -> {
			Map<NodeTuple,Double> weights = CMUtils.getConnsForNode(inputConnWeights, nodeNum);
			nodes.put(nodeNum, new Node(this, bias, weights));
		});
	}
	
	protected void normalize() {
		double mean = Stats.meanOutput(inputNodes.values());
		double sigma = Stats.sigma(inputNodes.values());
		inputNodes.forEach((tuple, node) -> normInputs.put(tuple, (node.getOutput() - mean)/sigma));
	}
	

	@Override
	public void run() {
		normalize();
		nodes.values().forEach(node -> node.run());
	}
	
	public void backProp() {
		nodes.forEach((nodeNum,node) -> node.backProp());
	}
}
