package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;
import utils.NodeTuple;

public class MidLayer extends UpperLayer {

	public MidLayer(Map<Integer, NodePhene> nodePhenes, Map<ConnTuple, Conn> conns, NeuralNetwork network, int layNum) {
		super(nodePhenes, conns, network, layNum);
	}
	
	protected UpperNode addNode(int nodeNum, NodePhene phene, Map<NodeTuple, Conn> nodeConns) {
		return new MidNode(this, nodeNum, phene.getBias(), nodeConns);
	}

	private class MidNode extends UpperNode {
		private double bias;

		MidNode(MidLayer layer, int nodeNum, double bias, Map<NodeTuple, Conn> conns) {
			super(layer, nodeNum, conns);
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

		@Override
		public void updateDerivative() {
			// TODO Auto-generated method stub
			
		}

	}

}
