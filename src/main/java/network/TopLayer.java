package network;

import java.util.Hashtable;
import java.util.Map;

import ecology.Species;
import utils.CMUtils;
import utils.ConnTuple;
import utils.NodeTuple;

public class TopLayer extends Layer {
	private static int nodeCount = Species.topNodes;
	double[] outputs = new double[nodeCount];
	
	public TopLayer(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights, NeuralNetwork network) {
		super(nodeBiases, inputConnWeights, network);
	}
	
	protected void fillNodes(Map<Integer,Double> nodeBiases, Map<ConnTuple,Double> inputConnWeights) {
		nodeBiases.forEach((nodeNum, bias) -> {
			Map<NodeTuple,Double> weights = CMUtils.getConnsForNode(inputConnWeights, nodeNum);
			nodes.put(nodeNum, new TopNode(this, bias, weights));
		});
	}
	
	@Override
	public void run() {
		super.run();
		double sum = nodes.values().stream().mapToDouble(d->d.output).sum();
//		System.out.println(sum);
//		System.out.println("[");
		nodes.forEach((nodeNum,node)-> {
//			System.out.print(node.output + ", ");
			outputs[nodeNum] = node.output/sum;
		});
//		System.out.println("]");
	}
	
//	@Override
//	public void backProp() {
//		nodes.forEach((nodeNum,node) -> node.backProp());
//	}
	
	private class TopNode extends Node {
		double error;

		TopNode(Layer layer, double bias, Map<NodeTuple, Double> weights) {
			super(layer, bias, weights);
		}
		
//		@Override
//		public void backProp() {
//			derivative = 2*error;
//		}
		
	}

}
