package network;

public abstract class Node implements Runnable {
	public double output = 0;
	Layer layer;
	NeuralNetwork network;
	
	public abstract void run();
	
}
