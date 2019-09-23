package network;

public abstract class Node implements Runnable {
	protected double output = 0;
	
	public double getOutput() {
		return output;
	}
	
	public abstract void run();
	
//	public abstract void backProp();
}
