package data;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import ecology.Species;

public class MnistDataReader {
	private static String folderPath = "src/main/resources/data/mnist/";
	private static String imageSuffix = "-images.idx3-ubyte";
	private static String labelSuffix = "-labels.idx1-ubyte";
	private String pathHeader;
	
	
	public MnistDataReader(String sourceFolder) {
		this.pathHeader = folderPath + sourceFolder;
	}
	
	public MnistImage[] readTrainingData() throws IOException {
		return readData("/train");
	}
	
	public MnistImage[] readTestingData() throws IOException {
		return readData("/t10k");
	}

	public MnistImage[] readData(String prefix) throws IOException {
		DataInputStream dataStream = new DataInputStream(
				new BufferedInputStream(new FileInputStream(pathHeader + prefix + imageSuffix)));
		@SuppressWarnings("unused")
		int imageMagicNum = dataStream.readInt();
		int imageCount = dataStream.readInt();
		int pixelCount = dataStream.readInt();
		
		Species.bottomWidth = pixelCount;

		DataInputStream labelStream = new DataInputStream(
				new BufferedInputStream(new FileInputStream(pathHeader + prefix + labelSuffix)));
		@SuppressWarnings("unused")
		int labelMagicNum = labelStream.readInt();
		int labelCount = labelStream.readInt();

		assert imageCount == labelCount;
	
		MnistImage[] matrices = new MnistImage[imageCount];
		byte[] labels = new byte[labelCount];
		labelStream.readFully(labels);
		for (int i = 0; i < imageCount; i++) {
			byte[][] byteData = new byte[pixelCount][pixelCount];
			for (int j = 0; j < pixelCount; j++) {
				byte[] row = new byte[pixelCount];
				dataStream.readFully(row);
				byteData[j] = row;
			}
			MnistImage mnistMatrix = new MnistImage(labels[i], byteData);
			matrices[i] = mnistMatrix;
		}
		Arrays.sort(labels);
		Species.topNodes = labels[labels.length-1]+1;
		
		dataStream.close();
		labelStream.close();
		return matrices;
	}
}
