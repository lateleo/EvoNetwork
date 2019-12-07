package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;
import utils.NodeVector;

public class MidLayer extends UpperLayer {


	public MidLayer(Map<NodeVector, NodePhene> nodePhenes, Map<ConnTuple, Connection> conns, NeuralNetwork network, int layNum) {
		super(nodePhenes, conns, network, layNum);
	}
	
	protected UpperNode addNode(NodeVector vector, NodePhene phene, Map<ConnTuple, Connection> conns) {
		return new MidNode(this, vector, phene, conns);
	}

}
