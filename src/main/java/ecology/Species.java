package ecology;


/*
 * This class is largely a variable dump. It doesn't do anything, except hold various parameters
 * that we may want to tweak between simulations. These parameters will be defined when the simulation
 * first runs, and will be referenced here and there by various classes that need them.
 */
public abstract class Species {
	public static int bottomNodes;
	public static int topNodes;
	public static int chromosomeCount;
	public static int startingLayers;
	public static double mutationRate;
	public static double slipChance;
	
	public void setup(int bottomNodes, int topNodes, int chromosomeCount, int startingLayers, double mutationRate, double slipChance) {
		Species.bottomNodes = bottomNodes;
		Species.topNodes = topNodes;
		Species.chromosomeCount = chromosomeCount;
		Species.startingLayers = startingLayers;
		Species.mutationRate = mutationRate;
		Species.slipChance = slipChance;
	}

}
