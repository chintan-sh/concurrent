package Assignment_1;

import java.util.LinkedList;

/**
 * Given a <code>LinkedList</code>, this class will find the average over a
 * subset of its <code>Integers</code>.
 */
public class ParallelWalkingAverageWorker extends Thread {

	protected LinkedList<Integer> list;
	protected float currAvgValue = 0; // initialize to lowest value
	protected int tid;

	public ParallelWalkingAverageWorker(LinkedList<Integer> list, int threadId) {
		this.list = list;
		this.tid = threadId;
	}
	
	/**
	 * Update <code>calculateAvg</code> until the list is exhausted.
	 */
	public void run() {
		while (true) {
			int number;
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
				if (list.isEmpty())
					return; // list is empty

				if(currAvgValue == 0){
					currAvgValue = list.remove();
				}

				number = list.remove();
				System.out.println("Number found for thread " + this.tid + " is "+ number);
				calculateAvg(getCurrAvgValue(), number);
			}
		}
	}

	public void calculateAvg(float val1, int val2){
		currAvgValue = (val1 + val2) / 2; //System.out.println("Current avg value encountered " + currAvgValue);
	}
	
	public float getCurrAvgValue() {
		return currAvgValue;
	}

}
