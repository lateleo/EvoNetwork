package network;

import java.util.Hashtable;
import java.util.Map;

import ecology.Species;
import utils.CMUtils;
import utils.ConnSetPair;
import utils.ConnTuple;
import utils.NodeTuple;

public class TopLayer extends UpperLayer {
	private static int nodeCount = Species.topNodes;
	double[] outputs = new double[nodeCount];
	double loss;
	
	public TopLayer(Map<Integer,ConnSetPair> pairs, Map<ConnTuple,Double> weights, NeuralNetwork network) {
		super(pairs, weights, network, -1);
		fillNodes(weights, pairs);
	}
	
	protected void fillNodes(Map<ConnTuple,Double> weights, Map<Integer,ConnSetPair> pairs) {
		pairs.forEach((nodeNum, pair) -> {
			Map<NodeTuple,Double> nodeWeights = getConnsForNode(weights, pair);
			nodes.put(nodeNum, new TopNode(this, nodeNum, nodeWeights));
		});
	}
	
	@Override
	public void run() {
		super.run();
		double sum = 0.0;
		for (Node node : nodes.values()) sum += node.output;
		sum = Math.max(sum, Double.MIN_NORMAL);
		nanCheck(sum, "Top Layer SoftMax Sum: ");
		for (Node node : nodes.values()) {
			node.output /= sum;
			nanCheck(node.output, "Top Node Post-SoftMax: ");
		}
		int label = network.currentImage.getLabel();
		Node correct = nodes.get(label);
		correct.output = 1 - correct.output;
		for (Node node : nodes.values()) ((TopNode) node).updateError();;
	}
	
	public void setLoss() {
		nodes.forEach((nodeNum,node) -> {
			loss += ((TopNode) node).error;
		});
	}
	
	private class TopNode extends UpperNode {
		double error = 0.0;
		

		TopNode(TopLayer layer, int nodeNum, Map<NodeTuple, Double> weights) {
			super(layer, nodeNum, weights);
		}
		
		public void updateError() {
			double sqrOut = output*output;
			nanCheck(sqrOut, "Top Node Output Squared: ");
		}
		
		@Override
		public void run() {
			output = 0;
			super.run();
		}
		
		@Override
		public void backProp() {
			derivative = 2*error;
			super.backProp();
			error = 0;
		}
		
	}

}
