package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;

public class TopLayer extends UpperLayer {
	
	public TopLayer(Map<Integer,NodePhene> nodePhenes, Map<ConnTuple,Connection> conns, NeuralNetwork network) {
		super(nodePhenes, conns, network);
	}
	

	@Override
	protected UpperNode addNode(int nodeNum, NodePhene phene, Map<ConnTuple, Connection> conns) {
		return new TopNode(this, nodeNum, phene, conns);
	}

	@Override
	public void run() {
		super.run();
		double sum = 0;
		for (Node node : nodes) sum += node.getOutput();
		sum = Math.max(sum, Double.MIN_NORMAL);
		nanCheck(sum, "Top Layer SoftMax Sum");
		int label = network.currentImage.getLabel();
		for (Node node : nodes) ((TopNode) node).updateError(sum, label);
	}

	
	public double getLoss() {
		double loss = 0;
		for (Node node : nodes) loss += ((TopNode) node).getLoss();
		return loss;
	}

}
