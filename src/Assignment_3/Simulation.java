package Assignment_3;

import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author : Chintan
 *
 * **/

/**
 * Simulation is the main class used to run the simulation.  You may
 * add any fields (static or instance) or any methods you wish.
 */
public class Simulation {
	// List to track simulation events during simulation
	public static List<SimulationEvent> events;
	public static List<Customer> currentCustomers;
	public static List<Customer> completedOrder;
	public static int availableTables;
	public static Machine coffeeMachine;
	public static Machine friesMachine;
	public static Machine burgerMachine;

	/* Stat variables */

	// Should not have more eaters than specified
	public static int customersEntered = 0;
	public static int customersLeft = 0;

	// Should not have more cooks than specified
	public static int cooksInitiated = 0;
	public static int cooksCreated = 0;

	// The coffee shop capacity should not be exceeded
	public static int tablesAvailableAtStart;

	//The capacity of each machine should not be exceeded
	public static int totalOrderPlacedForMachines = 0;
	public static int totalOrdersProcessedByMachines = 0;

	//Eater should not place more than one order
	public static int customersPlacedOrder = 0;
	public static int customersReceivedOrder = 0;

	/**
	 * Used by other classes in the simulation to log events
	 * @param event
	 */
	public static void logEvent(SimulationEvent event) {
		events.add(event);
		System.out.println(event);
	}

	/**
	 * 	Function responsible for performing the simulation. Returns a List of 
	 *  SimulationEvent objects, constructed any way you see fit. This List will
	 *  be validated by a call to Validate.validateSimulation. This method is
	 *  called from Simulation.main(). We should be able to test your code by 
	 *  only calling runSimulation.
	 *  
	 *  Parameters:
	 *	@param numCustomers the number of customers wanting to enter the coffee shop
	 *	@param numCooks the number of cooks in the simulation
	 *	@param numTables the number of tables in the coffee shop (i.e. coffee shop capacity)
	 *	@param machineCapacity the capacity of all machines in the coffee shop
	 *  @param randomOrders a flag say whether or not to give each customer a random order
	 *
	 */
	public static List<SimulationEvent> runSimulation(int numCustomers, int numCooks,int numTables, int machineCapacity,boolean randomOrders) {
		events = Collections.synchronizedList(new ArrayList<SimulationEvent>());

		// Start the simulation
		logEvent(SimulationEvent.startSimulation(numCustomers, numCooks, numTables, machineCapacity));

		// Set things up you might need
		availableTables = numTables;
		currentCustomers = new ArrayList<>();
		completedOrder  = new ArrayList<>();

		// Stat purposes
		tablesAvailableAtStart = availableTables;

		// Start up machines
		coffeeMachine = new Machine("Coffee Machine",FoodType.coffee,machineCapacity);
		Simulation.logEvent(SimulationEvent.machineStarting(coffeeMachine, FoodType.coffee, machineCapacity));

		friesMachine = new Machine("Fries Machine",FoodType.fries,1);
		Simulation.logEvent(SimulationEvent.machineStarting(friesMachine, FoodType.fries, machineCapacity));

		burgerMachine = new Machine("Burger Machine",FoodType.burger,1);
		Simulation.logEvent(SimulationEvent.machineStarting(burgerMachine, FoodType.burger, machineCapacity));

		// Let cooks in
		Thread[] cooks = new Thread[numCooks];
		for(int i = 0; i < cooks.length; i++) {
			cooks[i] = new Thread( new Cook("Cook " + (i+1))); //, coffeeMachine, friesMachine, burgerMachine
			cooksInitiated++;
			cooks[i].start();
		}

		// Build the customers.
		Thread[] customers = new Thread[numCustomers];

		// Create order list
		LinkedList<Food> order;

		if (!randomOrders) {
			// add coffee, fried and burger to the list
			order = new LinkedList<Food>();
			order.add(FoodType.burger);
			order.add(FoodType.fries);//order.add(FoodType.fries);
			order.add(FoodType.coffee);

			// create customer threads
			for(int i = 0; i < customers.length; i++) {
				customers[i] = new Thread( new Customer("Customer " + (i+1), order, 1));
			}
		}else {
			for(int i = 0; i < customers.length; i++) {
				Random rnd = new Random(27);
				int burgerCount = rnd.nextInt(3);
				int friesCount = rnd.nextInt(3);
				int coffeeCount = rnd.nextInt(3);
				order = new LinkedList<Food>();
				for (int b = 0; b < burgerCount; b++) {
					order.add(FoodType.burger);
				}
				for (int f = 0; f < friesCount; f++) {
					order.add(FoodType.fries);
				}
				for (int c = 0; c < coffeeCount; c++) {
					order.add(FoodType.coffee);
				}
				customers[i] = new Thread(new Customer("Customer " + (i+1), order, 1));
			}
		}


		// Now "let the customers know the shop is open" by
		//    starting them running in their own thread.
		for(int i = 0; i < customers.length; i++) {
			customers[i].start();
		}


		try {
			for(int i = 0; i < customers.length; i++) {
				customers[i].join();
			}

			// Then send cooks home...
			// The easiest way to do this might be the following, where
			// we interrupt their threads.  There are other approaches
			// though, so you can change this if you want to.
			for(int i = 0; i < cooks.length; i++)
				cooks[i].interrupt();
			for(int i = 0; i < cooks.length; i++)
				cooks[i].join();

		}catch(InterruptedException e) {
			System.out.println("Simulation thread interrupted.");
		}

		// Shut down machines
		logEvent(SimulationEvent.machineEnding(coffeeMachine));
		logEvent(SimulationEvent.machineEnding(burgerMachine));
		logEvent(SimulationEvent.machineEnding(friesMachine));

		System.out.println(" -------------------------------------- Stat checker --------------------------------------");
		System.out.println("Customer entered : " + customersEntered + " vs Customers left : " + customersLeft);
		System.out.println("Cooks created : " + cooksInitiated + " vs Cooks initiated : " + cooksCreated);
		System.out.println("Capacity at start : " + tablesAvailableAtStart + " vs Actual Capacity : " + availableTables);
		System.out.println("Total orders placed to machines : " + totalOrderPlacedForMachines + " vs Total orders processed by machines :  " + totalOrdersProcessedByMachines);
		System.out.println("TOtal customers who placed Orders : " + customersPlacedOrder + " vs Customers who received Orders : " + customersReceivedOrder);


		// Done with simulation		
		logEvent(SimulationEvent.endSimulation());

		return events;
	}

	/**
	 * Entry point for the simulation.
	 *
	 * @param args the command-line arguments for the simulation.  There
	 * should be exactly four arguments: the first is the number of customers,
	 * the second is the number of cooks, the third is the number of tables
	 * in the coffee shop, and the fourth is the number of items each cooking
	 * machine can make at the same time.  
	 */
	public static void main(String args[]) throws InterruptedException {
		// Parameters to the simulation
		/*
		if (args.length != 4) {
			System.err.println("usage: java Simulation <#customers> <#cooks> <#tables> <capacity> <randomorders");
			System.exit(1);
		}
		int numCustomers = new Integer(args[0]).intValue();
		int numCooks = new Integer(args[1]).intValue();
		int numTables = new Integer(args[2]).intValue();
		int machineCapacity = new Integer(args[3]).intValue();
		boolean randomOrders = new Boolean(args[4]);
		 */
		int numCustomers = 5; //10
		int numCooks = 3;
		int numTables = 2; // 5
		int machineCapacity = 3; // 4
		boolean randomOrders = false;


		// Run the simulation and then 
		//   feed the result into the method to validate simulation.
		System.out.println("Did it work? " + 
				Validate.validateSimulation(
						runSimulation(
								numCustomers, numCooks, 
								numTables, machineCapacity,
								randomOrders
								)
						)
				);
	}

}



