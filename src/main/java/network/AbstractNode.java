package network;

public abstract class AbstractNode implements Runnable {
	protected double output;
	
	public double getOutput() {
		return output;
	}
	
	public abstract void run();
	
//	public abstract void backProp();
}
