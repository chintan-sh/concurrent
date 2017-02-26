package Assignment_3;

import java.util.List;



/**
 *
 * @author : Chintan
 *
 * **/

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;
	private final int priority;
	private final int eatTime = 200;
	private final static Object lockA  = new Object();
	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order, int priority) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
		this.priority = priority;
	}

	public String toString() {
		return name;
	}

	public List<Food> getOrder(){
		return this.order;
	}

	public int getOrderNumber(){
		return this.orderNum;
	}
	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//YOUR CODE GOES HERE...
		System.out.println(SimulationEvent.customerStarting(this));
		enterCoffeeShop();
		placeOrder();
		receiveOrder();
		leaveCoffeeShop();
	}


	private void enterCoffeeShop(){
		synchronized (lockA){
			System.out.println("Tables available when trying to enter : " +  Simulation.availableTables);
			while(Simulation.availableTables < 1){
				try {
					lockA.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Simulation.customersEntered++;
			System.out.println(SimulationEvent.customerEnteredCoffeeShop(this));
			Simulation.availableTables--;
			System.out.println("Tables left after acquiring  one : " +  Simulation.availableTables);
		}
	}


	private void placeOrder(){
		// get lock and place an order
		synchronized (Simulation.currentCustomers) {
			// admitted inside order queue, welcome fellow free loader, come!
			Simulation.currentCustomers.add(this);
			Simulation.customersPlacedOrder++;
			System.out.println(SimulationEvent.customerPlacedOrder(this, order, orderNum));

			// let cooks know that order is placed
			System.out.println("---------------- Notifying cooks about order placed");
			Simulation.currentCustomers.notifyAll();
		}

	}


	private void receiveOrder(){
		// keep checking this guy and see if order is ready
		synchronized (Simulation.completedOrder) {
			// keep probing until current customer is part of order completed list
			while(!Simulation.completedOrder.contains(this)){
				try {
					Simulation.completedOrder.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(SimulationEvent.customerReceivedOrder(this, order, orderNum));
			Simulation.customersReceivedOrder++;
			// consume order, eat slowly - its all free
			try {
				Thread.sleep(eatTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Simulation.completedOrder.notifyAll();
		}


	}


	private void leaveCoffeeShop(){
		synchronized (lockA){
			// improve table count
			Simulation.availableTables++;
			System.out.println(SimulationEvent.customerLeavingCoffeeShop(this));
			Simulation.customersLeft++;
			lockA.notifyAll();
		}
	}


}