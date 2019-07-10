package network;

import java.util.TreeMap;

public class NeuralNetwork extends TreeMap<Integer, Layer> implements Runnable {
	private BottomLayer bottom;
	private TopLayer top;

	@Override
	public void run() {
		forEach((layNum, layer) -> layer.run());
	}
	
	void setBottom(BottomLayer bottom) {
		if (this.bottom.equals(null)) {
			this.bottom = bottom;
			put(0, bottom);
		}
	}
	
	void setTop(TopLayer top) {
		if (this.top.equals(null)) {
			this.top = top;
			put(-1,top);
		}
	}
	
}
