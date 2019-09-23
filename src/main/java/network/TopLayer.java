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
	
	public TopLayer(Map<Integer,ConnSetPair> pairs, Map<ConnTuple,Double> weights, NeuralNetwork network) {
		super(pairs, weights, network);
		fillNodes(weights, pairs);
	}
	
	protected void fillNodes(Map<ConnTuple,Double> weights, Map<Integer,ConnSetPair> pairs) {
		pairs.forEach((nodeNum, pair) -> {
			Map<NodeTuple,Double> nodeWeights = getConnsForNode(weights, pair);
			nodes.put(nodeNum, new TopNode(this, nodeWeights));
		});
	}
	
	@Override
	public void run() {
		super.run();
		double sum = Math.max(nodes.values().stream().mapToDouble(d->d.output).sum(), Double.MIN_VALUE);
		nodes.forEach((nodeNum,node)-> {
			outputs[nodeNum] = node.output/sum;
		});
	}
	
	private class TopNode extends UpperNode {
		double error;

		TopNode(TopLayer layer, Map<NodeTuple, Double> weights) {
			super(layer, weights);
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
		}
		
	}

}
