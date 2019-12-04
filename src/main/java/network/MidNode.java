package network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;

public class MidNode extends UpperNode {
	private List<Connection> upConns;
	private double bias;

	MidNode(MidLayer layer, int nodeNum, NodePhene phene, Map<ConnTuple, Connection> conns) {
		super(layer, nodeNum, phene, conns);
		this.bias = phene.getBias();
		getUpConns(conns, phene);
	}
	
	private void getUpConns(Map<ConnTuple, Connection> source, NodePhene phene) {
		upConns = new ArrayList<>();
		for (ConnTuple cTuple : phene.upConns) {
			Connection conn = source.get(cTuple);
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
		for (Connection conn : upConns) {
			UpperNode upNode = conn.upNode();
			derivative += conn.oldWeight()*upNode.derivative;
		}
	}

}
