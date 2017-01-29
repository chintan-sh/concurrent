package Assignment_1;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PublicAverageTest {

	private int	threadCount = 2; // number of threads to run
	private ParallelWalkingAverage averageMaker = new ParallelWalkingAverage(threadCount);
	
	@Test
	public void compareAverage() {
		int size = 10; // size of list
		LinkedList<Integer> list = new LinkedList<Integer>();
		Random rand = new Random();
		int sum = 0;
		float serialAvg = 0;
		float parallelAvg = 0;

		// populate list with random elements
		for (int i=0; i<size; i++) {
			int next = rand.nextInt(1)+1;
			System.out.println("Number generated :- " + next);
			list.add(next);
			sum = sum + next;
		}

		// calculating average serially
		serialAvg = sum / size;

		try {
			parallelAvg = averageMaker.avg(list);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("The test failed because the max procedure was interrupted unexpectedly.");
		} catch (Exception e) {
			e.printStackTrace();
			fail("The test failed because the max procedure encountered a runtime error: " + e.getMessage());
		}

		assertEquals(serialAvg, parallelAvg, 0);
	}
}
