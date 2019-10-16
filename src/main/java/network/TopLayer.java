package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;

public class TopLayer extends UpperLayer {
	
	public TopLayer(Map<Integer,NodePhene> nodePhenes, Map<ConnTuple,Conn> conns, NeuralNetwork network) {
		super(nodePhenes, conns, network, -1);
	}
	

	@Override
	protected UpperNode addNode(int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns) {
		return new TopNode(this, nodeNum, phene, conns);
	}

	@Override
	public void run() {
		super.run();
		softMax();
		int label = network.currentImage.getLabel();
		Node correct = nodes.get(label);
		correct.output = correct.output - 1;
		for (Node node : nodes.values()) ((TopNode) node).updateError();
	}
	
	private void softMax() {
		double sum = 0;
		for (Node node : nodes.values()) sum += node.output;
		final double finalSum = Math.max(sum, Double.MIN_NORMAL);
		nanCheck(sum, "Top Layer SoftMax Sum: ");
		nodes.forEach((nodeNum,node) -> {
			node.output /= finalSum;
			nanCheck(node.output, "Top Node Post-SoftMax: " + nodeNum);

		});
	}
	
	public double getLoss() {
		double loss = 0;
		for (Node node : nodes.values()) loss += ((TopNode) node).loss;
		return loss;
	}
	
	public void reset() {
		for (Node node : nodes.values()) {
			((TopNode) node).reset();
		}
	}
	
	private class TopNode extends UpperNode {
		double error = 0;
		double loss = 0;
		

		TopNode(TopLayer layer, int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns) {
			super(layer, nodeNum, phene, conns);
		}
		
		public void updateError() {
			error += output;
			double sqrOut = output*output;
			nanCheck(sqrOut, "Top Node Output Squared: ");
			loss += sqrOut;
		}
		
		public void reset() {
			error = 0;
			loss = 0;
		}
		
		@Override
		public void run() {
			output = 0;
			super.run();
		}

		@Override
		public void updateDerivative() {
			derivative = 2*error*invBatchSize;			
		}
		
	}

}
