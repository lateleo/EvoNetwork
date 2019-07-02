package utils;

import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class RNG {
	private static Random random = new Random();
	private static DoubleStream doubles = random.doubles();
	private static DoubleStream shiftDoubles = random.doubles(-1.0, 1.0);
	private static IntStream longBits = random.ints(0, 63);
	
	public static double getShiftDouble() {
		return shiftDoubles.findAny().getAsDouble();
	}
	
	public static double getDouble() {
		return doubles.findAny().getAsDouble();
	}
	
	public static int getLongBits() {
		return longBits.findAny().getAsInt();
	}
	
	public static boolean getBoolean() {
		return random.nextBoolean();
	}
}
