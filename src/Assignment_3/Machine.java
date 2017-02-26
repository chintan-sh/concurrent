package Assignment_3;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author : Chintan
 *
 * **/


/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;
	public List<Food> machineProcessingList;
	public final int capacity;


	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		this.capacity = capacityIn;
		this.machineProcessingList = new ArrayList<>();
	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public void makeFood(Cook currCook, int orderNum) throws InterruptedException {
		// add food item to processing list
		//machineProcessingList.add(machineFoodType);

		// ask machine to start cooking with cook's refernce & ordernum for return
		CookAnItem cookItem = new CookAnItem(currCook, orderNum);
		Thread itemThread = new Thread(cookItem);
		itemThread.start();
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		public int orderNum;
		public Cook currCook;


		// set these two for future purposes
		CookAnItem( Cook currCook, int orderNum){
			this.orderNum = orderNum;
			this.currCook = currCook;
		}

		public void run() {
			try {
				//start cooking
				Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));

				// about to sleep for cooking time - bye!
				Thread.sleep(machineFoodType.cookTimeMS);

				// food is cooked
				Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));

				//once food is cooked, add to completedOrders and ask the cook to take it
				synchronized (currCook.cookCompletedOrder){
					Simulation.totalOrdersProcessedByMachines++;
					currCook.cookCompletedOrder.add(machineFoodType);
					currCook.cookCompletedOrder.notifyAll();
				}

				// now remove the chap from our existing list making way for other orders
				synchronized (machineProcessingList){
					machineProcessingList.remove(0);
					machineProcessingList.notifyAll();
				}

			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String toString() {
		return machineName;
	}
}