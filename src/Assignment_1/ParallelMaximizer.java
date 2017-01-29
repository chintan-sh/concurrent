package Assignment_1;

import java.util.LinkedList;
import java.util.*;

/**
 * This class runs numThreads instances of
 * ParallelMaximizerWorker in parallel to find the maximum
 * Integer in a LinkedList.
 */
public class ParallelMaximizer {
	int numThreads;
	ArrayList<ParallelMaximizerWorker> workers; // = new ArrayList<ParallelMaximizerWorker>(numThreads);

	public ParallelMaximizer(int numT) {
		numThreads = numT;
		workers = new ArrayList<ParallelMaximizerWorker>(numThreads);
	}

	public static void main(String[] args) {
		int numT = 4; // number of threads for the maximizer
		int numElements = 800000; // number of integers in the list
		
		ParallelMaximizer maximizer = new ParallelMaximizer(numT);
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		// populate the list
		// TODO: change this implementation to test accordingly
		for (int i=0; i<numElements; i++) 
			list.add(i);

		// run the maximizer
		try {
			System.out.println("Final Max value " + maximizer.max(list));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Finds the maximum by using numThreads instances of
	 * ParallelMaximizerWorker to find partial maximums and then
	 * combining the results.
	 * @param list <code>LinkedList</code> containing <code>Integers</code>
	 * @return Maximum element in the <code>LinkedList</code>
	 * @throws InterruptedException
	 */
	public int max(LinkedList<Integer> list) throws InterruptedException {
		int maximum = Integer.MIN_VALUE; // initialize max as lowest value
		System.out.println("Initial Max value is " + Integer.MIN_VALUE); // System.out.println("Worker Size " + workers.size());

		// run numThreads instances of ParallelMaximizerWorker //workers.size()
		for (int i=0; i < numThreads; i++) {
			//System.out.print("Current worker " + i + "----------------------------------------- ");
			workers.add(i,new ParallelMaximizerWorker(list, i)); //workers.set(i, new ParallelMaximizerWorker(list)); //workers.get(i).run();
			workers.get(i).start();
		}

		// wait for threads to finish
		for (int i=0; i< numThreads; i++) { //workers.size()
			workers.get(i).join();
			System.out.print("Max found for worker " + i + " is : " + workers.get(i).getPartialMax() + "\n");
		}

		// take the highest of the partial maximums // TODO: IMPLEMENT CODE HERE
		LinkedList<Integer> partialMaxList = new LinkedList<Integer>();
		int calculatedMax = 0;
		for (int i=0; i< numThreads; i++) { //workers.size()
			if( workers.get(i).getPartialMax() > maximum) {
				partialMaxList.add(workers.get(i).getPartialMax());
				calculatedMax = Math.max(calculatedMax, workers.get(i).getPartialMax());
			}
		}
		System.out.print("Partial max list size :- " +  partialMaxList.size() + "\n");


		maximum = calculatedMax;
		System.out.println("Post all iterations, max value found : " + maximum);

		return maximum;
	}
	
}




		/* Using recursion to parallelize further calculations*/
//		if(partialMaxList.size() > 1){
//			System.out.print("\n<------ About to roll on recursion ------->  \n ");
//			this.max(partialMaxList);
//			System.exit(0);
//		}
//		maximum = partialMaxList.remove();

//		if(partialMaxList.size() == 1) {
//				System.out.print("Finally, Partial max size list " + partialMaxList.size() + "\n");
////			System.out.println("0th guy " + partialMaxList.get(0));
////			System.out.println("1st guy " + partialMaxList.get(1));
////			System.out.println("2nd guy " + partialMaxList.get(2));
////			System.out.println("3rd guy " + partialMaxList.get(3));
//
//				}