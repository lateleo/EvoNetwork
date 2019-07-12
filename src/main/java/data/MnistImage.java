package data;

public class MnistImage {
	private int size;
	private int label;
	private int[] data;

	public MnistImage(int label, byte[] byteData) {
		this.label = label;
		size = byteData.length;
		data = new int[size];
		for (int i = 0; i < size; i++) {
			data[i] = Byte.toUnsignedInt(byteData[i]);
		}
	}

	public int[] getData() {
		return data;
	}

	public int getValue(int i) {
		return data[i];
	}

	public int getLabel() {
		return label;
	}

	public int getSize() {
		return size;
	}

}
