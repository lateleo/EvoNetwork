package ecology;

public class Environment {

	public static void main(String[] args) {
		System.out.println("Start");
		
		double mRate = 0.05;
		double mMag = 1.25;
		double slip = 2;
		double lRate = 0.01;
		int diploidNum = 10;
		int layers = 3;
		int nodes = 25;
		int conns = 800;
		int popSize = 10;
		int simGens = 20;
		int batches = 600;
		double targetAccuracy = 0.9;
		
		long startTime = System.nanoTime();
		Species.initialize("original", batches);
		Population pop = Species.createPopulation(mRate, mMag, slip, lRate, diploidNum, layers, nodes, conns, popSize, simGens);
		System.out.println("Simulating First Few Generations...");
		pop.simulateGenerations();
		pop.iterate(targetAccuracy);
		
		long endTime = System.nanoTime();
		long seconds = (endTime - startTime)/1000000000;
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
