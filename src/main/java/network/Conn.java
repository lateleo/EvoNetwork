package network;

/*
 * This class probably seems unnecessary, but is used in place of the built-in Double class so that changes
 * made at one end of the connection will be visible at the other end.
 */
public class Conn {
	public double weight;
	
	public Conn(double weight) {
		this.weight = weight;
	}

}
