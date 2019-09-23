package network;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import utils.CMUtils;
import utils.ConnSetPair;
import utils.ConnTuple;
import utils.NodeTuple;
import utils.Stats;

public class MidLayer extends UpperLayer {

	
	public MidLayer(Map<Integer,Double> biases, Map<Integer,ConnSetPair> pairs, Map<ConnTuple,Double> weights, NeuralNetwork network) {
		super(pairs, weights, network);
		fillNodes(biases, weights, pairs);
	}

	
	protected void fillNodes(Map<Integer,Double> biases, Map<ConnTuple,Double> weights, Map<Integer,ConnSetPair> pairs) {
		biases.forEach((nodeNum, bias) -> {
			Map<NodeTuple,Double> nodeWeights = getConnsForNode(weights, pairs.get(nodeNum));
			nodes.put(nodeNum, new MidNode(this, bias, nodeWeights));
		});
	}
	
	private class MidNode extends UpperNode {
		private double bias;
		
		MidNode(MidLayer layer, double bias, Map<NodeTuple, Double> weights){
			super(layer, weights);
			this.bias = bias;
		}

		@Override
		public void run() {
			output = bias;
			super.run();
		}
		
		@Override
		public void backProp() {
			bias += learningRate*derivative;
			super.backProp();
		}
		
	}
	
}
