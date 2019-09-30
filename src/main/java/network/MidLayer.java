package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;
import utils.NodeTuple;

public class MidLayer extends UpperLayer {

	public MidLayer(Map<Integer, NodePhene> nodePhenes, Map<ConnTuple, Double> weights, NeuralNetwork network, int layNum) {
		super(nodePhenes, weights, network, layNum);
		fillNodes(nodePhenes, weights);
	}

	protected void fillNodes(Map<Integer, NodePhene> nodePhenes, Map<ConnTuple, Double> weights) {
		nodePhenes.forEach((nodeNum, phene) -> {
			Map<NodeTuple, Double> nodeWeights = getConnsForNode(weights, phene);
			nodes.put(nodeNum, new MidNode(this, nodeNum, phene.getBias(), nodeWeights));
		});
	}

	private class MidNode extends UpperNode {
		private double bias;

		MidNode(MidLayer layer, int nodeNum, double bias, Map<NodeTuple, Double> weights) {
			super(layer, nodeNum, weights);
			this.bias = bias;
		}

		@Override
		public void run() {
			output = bias;
			super.run();
		}

		@Override
		public void backProp() {
			bias += learningRate * derivative;
			super.backProp();
		}

	}

}
