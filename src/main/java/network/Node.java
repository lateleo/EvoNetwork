package network;

public abstract class Node implements Runnable {
	protected double output = 0;
	Layer layer;
	NeuralNetwork network;
	
	public abstract void run();
	
	public double getOutput() {
		return output;
	}
	
}
