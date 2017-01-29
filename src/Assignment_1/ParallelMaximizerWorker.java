package Assignment_1;

import java.util.LinkedList;

/**
 * Given a <code>LinkedList</code>, this class will find the maximum over a
 * subset of its <code>Integers</code>.
 */
public class ParallelMaximizerWorker extends Thread {

	protected LinkedList<Integer> list;
	protected int partialMax = Integer.MIN_VALUE; // initialize to lowest value
	protected int tid;

	public ParallelMaximizerWorker(LinkedList<Integer> list, int threadId) {
		this.list = list;
		this.tid = threadId;
	}
	
	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
		while (true) {
			int number;
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
				if (list.isEmpty())
					return; // list is empty
				number = list.remove();
				//System.out.println("Number found for thread " + this.tid + " is "+ number);
			}

			// update partialMax according to new value |  TODO: IMPLEMENT CODE HERE
			if(number > getPartialMax()){
				partialMax = number;
			}
		}
	}
	
	public int getPartialMax() {
		return partialMax;
	}

}
