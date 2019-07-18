package network;

import java.util.Arrays;
import java.util.TreeMap;
import ecology.Species;

public class NeuralNetwork extends TreeMap<Integer, AbstractLayer> implements Runnable {
	private static final long serialVersionUID = -2513726838630426232L;
	private static int imageCount = Species.images.length;
	private BottomLayer bottom;
	private TopLayer top;
	private double accuracy;

	@Override
	public void run() {
		double loss = 0.0;
		while (!bottom.allImagesComplete()) {
			forEach((layNum, layer) -> layer.run());
			double[] outputs = top.outputs;
			int label = bottom.currentImage.getLabel();
			outputs[label] = 1.0 - outputs[label];
			for (int i = 0; i < outputs.length; i++) outputs[i] *= outputs[i];
			loss += Arrays.stream(outputs).sum();
		}
		accuracy = 1 - loss/(2.0*imageCount);
	}
	
	void setBottom(BottomLayer bottom) {
		if (this.bottom == null) {
			this.bottom = bottom;
			put(0, bottom);
		}
	}
	
	void setTop(TopLayer top) {
		if (this.top == null) {
			this.top = top;
			put(-1, top);
		}
	}
	
	public double getAccuracy() {
		return accuracy;
	}
	
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
}
