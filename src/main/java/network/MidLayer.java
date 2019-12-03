package network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;

public class MidLayer extends UpperLayer {

	public MidLayer(Map<Integer, NodePhene> nodePhenes, Map<ConnTuple, Conn> conns, NeuralNetwork network, int layNum) {
		super(nodePhenes, conns, network, layNum);
	}
	
	protected UpperNode addNode(int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns) {
		return new MidNode(this, nodeNum, phene, conns);
	}

	
	
	
	private class MidNode extends UpperNode {
		private List<Conn> upConns;
		private double bias;

		MidNode(MidLayer layer, int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns) {
			super(layer, nodeNum, phene, conns);
			this.bias = phene.getBias();
			getUpConns(conns, phene);
		}
		
		private void getUpConns(Map<ConnTuple, Conn> source, NodePhene phene) {
			upConns = new ArrayList<>();
			for (ConnTuple cTuple : phene.upConns) {
				Conn conn = source.get(cTuple);
				conn.setDownNode(this);
				upConns.add(conn);
			}
		}

		@Override
		public void run() {
			output = bias;
			super.run();
		}

		@Override
		public void backProp() {
			super.backProp();
			bias += learningRate * derivative;
		}

		@Override
		public void updateDerivative() {
			derivative = 0;
			for (Conn conn : upConns) {
				UpperNode upNode = conn.upNode();
				if (upNode.avgOutput != 0) derivative += conn.oldWeight()*upNode.derivative;
			}
		}

	}

}
