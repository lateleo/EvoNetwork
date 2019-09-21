package network;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import ecology.Species;

public class NeuralNetwork extends TreeMap<Integer, AbstractLayer> implements Runnable {
	private static final long serialVersionUID = -2513726838630426232L;
	private static int imageCount = Species.images[0].length;
	private BottomLayer bottom;
	private TopLayer top;
	private double accuracy = 0.0;
	
	public NeuralNetwork() {
		super(Species.comparator);
	}

	@Override
	public void run() {
		accuracy = 0.0;
		while (!bottom.allImagesComplete()) {
			forEach((layNum, layer) -> layer.run());
			int label = bottom.currentImage.getLabel();
			
			double[] outputs = top.outputs;
			outputs[label] = 1 - outputs[label];
			for (int i = 0; i < outputs.length; i++) outputs[i] *= outputs[i];
			accuracy += Arrays.stream(outputs).sum();
			
//			accuracy += top.outputs[label];
		}
		accuracy /= 2.0*imageCount;
		bottom.resetImageIndex();
		
//		accuracy /= imageCount;
	}
	
	public void backProp() {
		descendingKeySet().forEach((layNum) -> {
			if (layNum != 0) get(layNum).backProp();
		});
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
	
}
