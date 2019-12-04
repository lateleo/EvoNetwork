package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;

public class TopLayer extends UpperLayer {
	
	public TopLayer(Map<Integer,NodePhene> nodePhenes, Map<ConnTuple,Connection> conns, NeuralNetwork network) {
		super(nodePhenes, conns, network, -1);
	}
	

	@Override
	protected UpperNode addNode(int nodeNum, NodePhene phene, Map<ConnTuple, Connection> conns) {
		return new TopNode(this, nodeNum, phene, conns);
	}

	@Override
	public void run() {
		super.run();
		softMax();
		int label = network.currentImage.getLabel();
		Node correct = nodes.get(label);
		correct.output = correct.output - 1;
		for (Node node : nodes) ((TopNode) node).updateError();
	}
	
	private void softMax() {
		double sum = 0;
		for (Node node : nodes) sum += node.output;
		final double finalSum = Math.max(sum, Double.MIN_NORMAL);
		nanCheck(sum, "Top Layer SoftMax Sum: ");
		for (Node node : nodes) {
			node.output /= finalSum;
			nanCheck(node.output, "Top Node Post-SoftMax");
		}
	}
	
	public double getLoss() {
		double loss = 0;
		for (Node node : nodes) loss += ((TopNode) node).loss;
		return loss;
	}
	
	public void reset() {
		for (Node node : nodes) {
			((TopNode) node).reset();
		}
	}

}
