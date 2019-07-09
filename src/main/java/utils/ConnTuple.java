package utils;

import genetics.ConnGene;

/*
 * This class is used exclusively as a Key object for the 'conns' map that is produced by the Transcriptome.
 * It was originally a nested class within Transcriptome, but it will be useful when constructing the network 
 * from the Transcriptome as well, so it was moved here.
 */
public class ConnTuple implements Comparable<ConnTuple> {
	public int iLay, iNode, oLay, oNode;
	
	public ConnTuple(ConnGene gene) {
		this.iLay = (int) gene.inLayNum;
		this.iNode = (int) gene.inNodeNum;
		this.oLay = (int) Math.floor(gene.outLayNum); //This is different because negative doubles round up when cast to int
		this.oNode = (int) gene.outNodeNum;
	}
	
	boolean equals(ConnTuple other) {
		return this.iLay == other.iLay &&
				this.iNode == other.iNode &&
				this.oLay == other.oLay &&
				this.oNode == other.oNode;
	}
	
	public int hashCode() {
		return (Integer.toString(iLay) + "." +
				Integer.toString(iNode) + "." +
				Integer.toString(oLay) + "." +
				Integer.toString(oNode)).hashCode();
	}

	public int compareTo(ConnTuple other) {
		if (this.iLay != other.iLay) return this.iLay - other.iLay;
		else if (this.iNode != other.iNode) return this.iNode - other.iNode;
		else if (this.oLay != other.oLay) return this.oLay - other.oLay;
		else return this.oNode - other.oNode;
	}
}
