package network;

import java.util.Map;

import ecology.Species;
import genetics.NodePhene;
import utils.ConnTuple;
import utils.NodeTuple;

public class TopLayer extends UpperLayer {
	private static int nodeCount = Species.topNodes;
	double[] outputs = new double[nodeCount];
	double loss;
	
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
		double sum = 0.0;
		for (Node node : nodes.values()) sum += node.output;
		final double finalSum = Math.max(sum, Double.MIN_NORMAL);
		nanCheck(sum, "Top Layer SoftMax Sum: ");
		nodes.forEach((nodeNum,node) -> {
			node.output /= finalSum;
			nanCheck(node.output, "Top Node Post-SoftMax: " + nodeNum);

		});
//		for (Node node : nodes.values()) {
//			node.output /= sum;
//			nanCheck(node.output, "Top Node Post-SoftMax: ");
//		}
		int label = network.currentImage.getLabel();
		Node correct = nodes.get(label);
		correct.output = 1 - correct.output;
		for (Node node : nodes.values()) ((TopNode) node).updateError();
	}
	
	public void setLoss() {
		nodes.forEach((nodeNum,node) -> {
			loss += ((TopNode) node).error;
		});
	}
	
	private class TopNode extends UpperNode {
		double error = 0.0;
		

		TopNode(TopLayer layer, int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns) {
			super(layer, nodeNum, phene, conns);
		}
		
		public void updateError() {
			double sqrOut = output*output;
			nanCheck(sqrOut, "Top Node Output Squared: ");
			error += sqrOut;
		}
		
		@Override
		public void run() {
			output = 0;
			super.run();
		}
		
		@Override
		public void backProp() {
			super.backProp();
			error = 0;
		}

		@Override
		public void updateDerivative() {
			derivative = 2*error*invBatchSize;			
		}
		
	}

}
