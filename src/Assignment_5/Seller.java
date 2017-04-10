package Assignment_5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Seller implements Client{

	private static final int MaxItems = 100;
	private String name;
	private int cycles;
	private int maxSleepTimeMs;
	private List<String> items;
	private Random rand;
	private AuctionServer server;

	public Seller(AuctionServer server, String name, int cycles, int maxSleepTimeMs, long randomSeed){
		// get name provided by simulation loop
		this.name = name;

		// assign no of attempts i.e 100
		this.cycles = cycles;

		// how much should it sleep between attempts
		this.maxSleepTimeMs = maxSleepTimeMs;
		
		//  set starting point for generating random num (say if its 3, it'll pick up rnd num >=3)
		this.rand = new Random(randomSeed);

		// init max item count that a seller can enlist i.e 100
        int itemCount = MaxItems;

		// data store for trapping all items
        this.items = new ArrayList<String>();

		// generate 100 different items and add to data store
        for (int i = 0; i < itemCount; ++i){
            items.add(this.name() + "#" + i);
        }
        
        this.server = server;
	}
	
	@Override
	public String name(){
		return this.name;
	}

	@Override
    public void run(){
		// if cycle count not exceeded i.e 100 and item data store is not empty
		for (int i = 0; i < this.cycles && this.items.size() > 0; ++i){
	    	// fetch random item
			int index = this.rand.nextInt(this.items.size());

	    	String item = this.items.get(index);

			// submit item to auction fellow ( sellername, currItem,      startingPrice,     activeTimeBeforeTimeout)
	    	int listingID = server.submitItem(this.name(),  item,    this.rand.nextInt(100), this.rand.nextInt(1000) + 100);

			// if id returned > 1 (meaning it got accepted), remove it from data store
	    	if (listingID != -1){
	    		this.items.remove(index);
	    	}

	    	// now get cozy and goto sleep
    		try{
                Thread.sleep(this.rand.nextInt(this.maxSleepTimeMs));
            }
            catch (InterruptedException e){
				// if something happens, bleedy catch it
                e.printStackTrace();
                return;
            }
	    }
    }
}