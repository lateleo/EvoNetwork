package data;

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

	public int getValue(int x, int y) {
		return data[x][y];
	}

	public int getLabel() {
		return label;
	}

	public int getSize() {
		return size;
	}

}
