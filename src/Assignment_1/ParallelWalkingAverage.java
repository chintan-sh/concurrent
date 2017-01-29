package Assignment_1;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class runs numThreads instances of
 * ParallelWalkingAverageWorker in parallel to find the average of all
 * Integer in a LinkedList.
 */
public class ParallelWalkingAverage {
	int numThreads;
	ArrayList<ParallelWalkingAverageWorker> workers;

	public ParallelWalkingAverage(int numT) {
		numThreads = numT;
		workers = new ArrayList<ParallelWalkingAverageWorker>(numThreads);
	}

	public static void main(String[] args) {
		int numT = 8; // number of threads for the maximizer
		int numElements = 100; // number of integers in the list
		
		ParallelWalkingAverage averageMaker = new ParallelWalkingAverage(numT);
		LinkedList<Integer> list = new LinkedList<Integer>();

		// run the averagemaker
		try {
			// running it 10 times with same list
			for(int i=0; i<10; i++) {
				System.out.println("<--- Iteration " + (i+1) + " ------>");
				// populate the list
				for (int j=0; j<numElements; j++) {
					list.add(j);
				}

				// get the average
				System.out.println("Ultimate Average value " + averageMaker.avg(list));
				System.out.println("\n");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Finds the maximum by using numThreads instances of
	 * ParallelWalkingAverageWorker to find partial maximums and then
	 * combining the results.
	 * @param list <code>LinkedList</code> containing <code>Integers</code>
	 * @return Maximum element in the <code>LinkedList</code>
	 * @throws InterruptedException
	 */
	public float avg(LinkedList<Integer> list) throws InterruptedException {
		float average = 0; // initialize average as lowest value

		// run numThreads instances of ParallelWalkingAverageWorker
		for (int i=0; i < numThreads; i++) {
			workers.add(i,new ParallelWalkingAverageWorker(list, i));
			workers.get(i).start();
		}

		// wait for threads to finish
		for (int i=0; i< numThreads; i++) { //workers.size()
			workers.get(i).join();
			System.out.print("Avg found for worker " + i + " is : " + workers.get(i).getCurrAvgValue() + "\n");
		}

		// take individual average calculated by each thread and sum it// TODO: IMPLEMENT CODE HERE
		LinkedList<Float> partialAvgList = new LinkedList<Float>();
		float sum = 0;
		int threadCount = 0;
		for (int i=0; i< numThreads; i++) {
			if(workers.get(i).getCurrAvgValue() > 0){
				partialAvgList.add(workers.get(i).getCurrAvgValue());
				sum = sum + workers.get(i).getCurrAvgValue();
				threadCount++;
			}
		}

		average = sum / threadCount;
		return average;
	}
	
}