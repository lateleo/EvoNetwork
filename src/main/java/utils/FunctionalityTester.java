package utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;

import data.MnistDataReader;
import data.MnistImage;
import ecology.Species;
import genetics.Gene;
import network.BottomLayer;

/*
 * This class is used solely as a sandbox to test the functionality of various parts of the application.
 * Ultimately, this will probably be replaced with a proper testing framework, but this will be used for the time being.
 */
public class FunctionalityTester {

	public static void main(String[] args) throws IOException {

	}

	private static void mnistMatrixDemo() throws IOException {
		MnistDataReader reader = new MnistDataReader("original");
		MnistImage[] mnistMatrix = reader.readTrainingData();
//		printMnistMatrix(mnistMatrix[mnistMatrix.length - 1]);
//		mnistMatrix = reader.readTestingData();
//		printMnistMatrix(mnistMatrix[0]);
	}
	
	private static void printMnistMatrix(final MnistImage matrix) {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(0);
		format.setMinimumIntegerDigits(3);
		System.out.println("label: " + matrix.getLabel());
		for (int r = 0; r < 28; r++) {
			for (int c = 0; c < 28; c++) {
				System.out.print(format.format(matrix.getValue(r*28 + c)) + " ");
			}
			System.out.println();
		}
	}

}
