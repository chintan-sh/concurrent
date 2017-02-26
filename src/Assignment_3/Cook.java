package Assignment_3;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author : Chintan
 *
 * **/


/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	private Customer customer;
	public List<Food> cookCompletedOrder;
	private int currCustomerOrderNum;

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
		this.cookCompletedOrder = new ArrayList<Food>();
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {
		SimulationEvent.cookStarting(this);

		try {
			while (true) {
				// extract current customer and corresponding order
				synchronized (Simulation.currentCustomers) {
					// if no customer, keep waiting
					System.out.println("---------------- Current customer in cafe count " + Simulation.currentCustomers.size());
					while (Simulation.currentCustomers.size() < 1) {
						Simulation.currentCustomers.wait();
					}

					// once found, extract 1st guy and start
					customer =Simulation.currentCustomers.remove(0);
					currCustomerOrderNum = customer.getOrderNumber();
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, customer.getOrder(), currCustomerOrderNum));
					Simulation.currentCustomers.notifyAll();
				}

				// loop each order from current customer's order list
				for(Food oneOrder : customer.getOrder()){
					// for stats wonly
					Simulation.totalOrderPlacedForMachines++;

					if(oneOrder.name.equals("coffee")){
						// get machine's processing list
						synchronized (Simulation.coffeeMachine.machineProcessingList){
							// if list is filled, wait
							while(Simulation.coffeeMachine.machineProcessingList.size() >= Simulation.coffeeMachine.capacity){
								Simulation.coffeeMachine.machineProcessingList.wait();
							}

							// once place found on list, enlist the item for respective machine
							Simulation.coffeeMachine.machineProcessingList.add(oneOrder);

							// pass cook and ordernum to machine for referencing back
							Simulation.coffeeMachine.makeFood(this, currCustomerOrderNum);

							// let machine know, order is incoming
							Simulation.coffeeMachine.machineProcessingList.notifyAll();
						}
					}

					if(oneOrder.name.equals("fries")){
						synchronized (Simulation.friesMachine.machineProcessingList){
							while(Simulation.friesMachine.machineProcessingList.size() >= Simulation.friesMachine.capacity){
								Simulation.friesMachine.machineProcessingList.wait();
							}

							Simulation.friesMachine.machineProcessingList.add(oneOrder);
							Simulation.friesMachine.makeFood(this, currCustomerOrderNum);
							Simulation.friesMachine.machineProcessingList.notifyAll();
						}
					}

					if(oneOrder.name.equals("burger")){
						synchronized (Simulation.burgerMachine.machineProcessingList){
							while(Simulation.burgerMachine.machineProcessingList.size() >= Simulation.burgerMachine.capacity){
								Simulation.burgerMachine.machineProcessingList.wait();
							}

							Simulation.burgerMachine.machineProcessingList.add(oneOrder);
							Simulation.burgerMachine.makeFood(this, currCustomerOrderNum);
							Simulation.burgerMachine.machineProcessingList.notifyAll();
						}
					}

				}

				// now keep an eye on cook's completed order list (would be updated by machine on item-by-item basis)
				synchronized (cookCompletedOrder){
					// keep looping boss until total order count fits in
					while(cookCompletedOrder.size() < customer.getOrder().size()){
						cookCompletedOrder.wait();
						// let the machine know, there's still work to do
						cookCompletedOrder.notifyAll();
					}
				}

				// push customer and let him know that 'boss, your order is ready'
				synchronized (Simulation.completedOrder){
					Simulation.completedOrder.add(customer);
					Simulation.completedOrder.notifyAll();
				}



				//once, order is finished, reinit list for the next order
				cookCompletedOrder = new ArrayList<Food>();
			}

		}catch(InterruptedException e) {
//			 This code assumes the provided code in the Simulation class
//			 that interrupts each cook thread when all customers are done.
//			 You might need to change this if you change how things are
//			 done in the Simulation class.
			Simulation.cooksCreated++;
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}

}