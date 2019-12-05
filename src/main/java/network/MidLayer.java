package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;

public class MidLayer extends UpperLayer {
	private int layNum;


	public MidLayer(Map<Integer, NodePhene> nodePhenes, Map<ConnTuple, Connection> conns, NeuralNetwork network, int layNum) {
		super(nodePhenes, conns, network);
		this.layNum = layNum;
	}
	
	protected UpperNode addNode(int nodeNum, NodePhene phene, Map<ConnTuple, Connection> conns) {
		return new MidNode(this, nodeNum, phene, conns);
	}
	
	public int layNum() {
		return layNum;
	}

}
