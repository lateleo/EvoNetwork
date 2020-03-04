package ecology;

public class Environment {

	public static void main(String[] args) {
		System.out.println("Start");
		
		double mRateFactor = 0;
		double mMagFactor = 2;
		double slipFactor = 5;
		
		int haploidNum = 10;
		int layers = 2;
		int nodes = 15;
		int conns = 20;
		
		int topRedundancy = 10;
		int bottomRedundancy = 3;
		double dormancy = 0.5;
		int popSize = 10;
		int simGens = 5;
		int batchSize = 100;
		double targetAccuracy = 0.9;
		
		long startTime = System.currentTimeMillis();
		Species.initialize("original", batchSize);
		Population pop = Species.createPopulation(mRateFactor,  mMagFactor,  slipFactor, haploidNum,  layers,  nodes, conns,
				topRedundancy, bottomRedundancy, dormancy, popSize,  simGens);
		System.out.println("Simulating First Few Generations...");
		pop.simulateGenerations();
//		pop.iterate(targetAccuracy);
		pop.runEpoch(300);
		
		long endTime = System.currentTimeMillis();
		long seconds = (endTime - startTime)/1000;
		int minutes = 0;
		int hours = 0;
		while (seconds > 60) {
			minutes++;
			seconds -= 60;
		}
		while (minutes > 60) {
			hours++;
			minutes -= 60;
		}
		System.out.println("Training Done: " + hours + "h" + minutes + "m" + seconds + "s");
		pop.testGeneration();
		System.out.println("Done.");
	}

}
