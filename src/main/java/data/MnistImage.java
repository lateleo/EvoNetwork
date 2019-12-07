package data;

import utils.NodeVector;

public class MnistImage {
	private int size;
	private int label;
	private int[][] data;

	public MnistImage(int label, byte[][] byteData) {
		this.label = label;
		size = byteData.length;
		data = new int[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				data[x][y] = Byte.toUnsignedInt(byteData[x][y]);
			}
		}
	}

	public int[][] getData() {
		return data;
	}

	public int getValue(NodeVector vector) {
		int x = (int) (vector.getX() + size/2);
		int y = (int) (vector.getY() + size/2);
		return data[x][y];
	}

	public int getLabel() {
		return label;
	}

	public int getSize() {
		return size;
	}

}
