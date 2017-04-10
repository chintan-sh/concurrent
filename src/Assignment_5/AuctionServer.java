package Assignment_5;

/**
 *  @author YOUR NAME SHOULD GO HERE
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class AuctionServer {
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 *
	 * For test purposes, we made the constructor protected.
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer() {
	}

	private static AuctionServer instance = new AuctionServer();
	public static AuctionServer getInstance(){
		return instance;
	}
	/* Singleton: End code that you SHOULD NOT CHANGE! */




	/* Statistic variables and server constants: Begin code you should likely leave alone. */
	/**
	 * Server statistic variables and access methods:
	 */
	private AtomicInteger soldItemsCount = new AtomicInteger(0);
	private AtomicInteger revenue = new AtomicInteger(0);
	// count no of items submitted
	private AtomicInteger count = new AtomicInteger(0);


	public int soldItemsCount() {
		return this.soldItemsCount.get();
	}

	public int revenue() {
		return this.revenue.get();
	}

	public int count() {
		return this.count.get();
	}


	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.
	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private CopyOnWriteArrayList<Item> itemsUpForBidding = new CopyOnWriteArrayList(new ArrayList<Item>());

	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private AtomicInteger lastListingID = new AtomicInteger(-1);

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private ConcurrentHashMap<Integer, Item> itemsAndIDs = new ConcurrentHashMap<Integer, Item>();

	// List of sellers and how many items they have currently up for bidding.
	private ConcurrentHashMap<String, Integer> itemsPerSeller = new ConcurrentHashMap<String, Integer>();

	// List of sellers and no.of items they have currently up for bidding which are > $75
	private ConcurrentHashMap<String, Integer>  itemsPerSellerAbove75 = new ConcurrentHashMap<String, Integer>();

	// List of sellers who are disqualified
	private CopyOnWriteArrayList<String> blacklistedSellers = new CopyOnWriteArrayList(new ArrayList<String>());;

	// List of buyers and how many items on which they are currently bidding.
	private ConcurrentHashMap<String, Integer> itemsPerBuyer = new ConcurrentHashMap<String, Integer>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private ConcurrentHashMap<Integer, Integer> highestBids = new ConcurrentHashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	//private ConcurrentHashMap<Integer, String> highestBidders = new ConcurrentHashMap<Integer, String>();
	private ConcurrentHashMap<Integer, String> highestBidders = new ConcurrentHashMap<Integer, String>();


	/*
	 *  The code from this point forward can and should be changed to correctly and safely
	 *  implement the methods as needed to create a working multi-threaded server for the
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */
	public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs) {
		// Make sure seller is not blacklisted
		if (blacklistedSellers.contains(sellerName)) {
			//System.out.println(sellerName + " is blacklisted due to violation of rules ");
			return -1;
		}


		// Make sure seller has not listed more than 3 items for > $75 else add to blacklist
		if (itemsPerSellerAbove75.containsKey(sellerName)) {
			if (itemsPerSellerAbove75.get(sellerName) >= 3) { // i.e >= 3 items priced > $75
				//System.out.println(sellerName + " is disqualified and will be blacklisted ");
				blacklistedSellers.add(sellerName);
				return -1;
			}
		}

		// Make sure there's room in the auction site.
		if (itemsUpForBidding.size() < serverCapacity) { // i.e <= 80 items on server
			// If the seller is a new one, add them to the list of sellers and initialize with no.of items they have listed.
			if (!itemsPerSeller.containsKey(sellerName)) {
				itemsPerSeller.put(sellerName, 0);
			}

			// If the seller has too many items up for bidding, don't let them add this one.
			if (itemsPerSeller.get(sellerName) < maxSellerItems) { // i.e  <= 20 items/seller
				// get last listing id and increment by 1

				int lastID = this.incLastListingID();
				//lastListingID = new AtomicInteger(lastListingID.get() + 1);

				// create item
				Item currItem = new Item(sellerName, itemName, lastID, lowestBiddingPrice, biddingDurationMs);

				// list of all items and id for archival purposes
				itemsAndIDs.put(lastID, currItem);
				incCount();

				// add item amongst active items (menu)
				itemsUpForBidding.add(currItem);

				// increment seller item listed count
				itemsPerSeller.put(sellerName, itemsPerSeller.get(sellerName) + 1);

				// remove following comment
				//System.out.println("Item added and listing id is " + lastID + " uploaded by seller " + sellerName + " with price " + lowestBiddingPrice);

				// Starting price > 75, increase count
				if (lowestBiddingPrice > 75) {
					// check if this fellow is repeat offender
					if (!itemsPerSellerAbove75.containsKey(sellerName)) {
						itemsPerSellerAbove75.put(sellerName, 0);
					}

					// if yes, increment his felony count
					itemsPerSellerAbove75.put(sellerName, itemsPerSellerAbove75.get(sellerName) + 1);
					//System.out.println("Item price > $75 count for " + sellerName + " is " + itemsPerSellerAbove75.get(sellerName));
				}

				return lastID;
			}
		}

		//System.out.println("Sorry! Server capacity reached ");
		return -1;
	}



	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	public List<Item> getItems() {
		List<Item> freshItemList = new ArrayList<>();
		for (Item item : itemsUpForBidding) {
			if (item.biddingOpen()) {
				freshItemList.add(new Item(item.seller(), item.name(), item.listingID(), item.lowestBiddingPrice(),
						item.biddingDurationMs()));
			}
		}
		return freshItemList;

		//return new ArrayList<Item>(itemsUpForBidding);
	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	public boolean submitBid(String bidderName, int listingID, int biddingAmount) {
		// See if the item exists.
		if(itemsAndIDs.containsKey(listingID)){
			Item item = itemsAndIDs.get(listingID);

			// See if it can be bid upon.
			if(item.biddingOpen()){
				// if bidder is new, add to list with init value of 0
				if(!itemsPerBuyer.containsKey(bidderName)){
					itemsPerBuyer.put(bidderName, 0);
				}

				// See if this bidder has too many items in their bidding list.
				if(itemsPerBuyer.get(bidderName) < maxBidCount){ // i.e 10 bids
					// Get lowest bidding info
					int currBid = item.lowestBiddingPrice();

					// If item is already bid upon, get highest bid
					if(highestBids.containsKey(listingID)){
						currBid = highestBids.get(listingID);
					}

					// Check if item is bid upon by currentBidder
					if(highestBidders.containsKey(listingID)){
						// Make sure current bidder doesnt already hold the highest bid.
						if(highestBidders.get(listingID).equals(bidderName)){
							// remove following comment
							//System.out.println("Sorry! Bid for item with Listing ID " + listingID + " is already held by bidder " + bidderName);
							return false;
						}
					}


					// See if the new bid is better than the existing/opening bid floor.
					if(biddingAmount > currBid && item.biddingOpen()){
						// Decrement the former winning bidder's count
						if(highestBidders.containsKey(listingID)) {
							String previousBidder = highestBidders.get(listingID);
							itemsPerBuyer.put(previousBidder, itemsPerBuyer.get(previousBidder) - 1);
						}

						// Put your bid in place
						highestBids.put(listingID, biddingAmount); // put currBid in highest bid
						highestBidders.put(listingID, bidderName); // put currBidder as highestBIdder for this item
						itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName)+1); // increment bidderItem count by 1

						// remove following comment
						//System.out.println("Bid for item with Listing ID :- " + listingID + " by bidder :- " + bidderName + " with amount :- " + biddingAmount + " is successful \n");
						return true;
					}else{
						// remove following comment
						//System.out.println("NO! Bid is lower than highest bid" );
						return false;
					}
				}
				// remove following comment
				//System.out.println("Sorry! Bidder  " + bidderName + " has exceeded " + maxBidCount);
				return false;
			}
			// remove following comment
			//System.out.println("Sorry! Bidding for " + listingID + " is not open" );
			return false;
		}
		//System.out.println("-------------------------------------------> Sorry! Listing ID  " + listingID + " is invalid " + itemsAndIDs.get(listingID) + " <------------------------------------------- ");
		return false;
	}


	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	public int checkBidStatus(String bidderName, int listingID) {
		// Some reminders:
		//   If the bidding is closed, clean up for that item.
		//     Remove item from the list of things up for bidding.
		//     Decrease the count of items being bid on by the winning bidder if there was any...
		//     Update the number of open bids for this seller

		// See if the item exists.
		if (itemsAndIDs.containsKey(listingID)) {
			Item item = itemsAndIDs.get(listingID);

			// See if it can be bid upon.
			if (!item.biddingOpen()) {

				// If item is in active list inspite of being timedout
				// remove item from active bid list
				itemsUpForBidding.remove(item);

				// decrement seller item listed count
				itemsPerSeller.put(item.seller(), itemsPerSeller.get(item.seller()) - 1);

				// decrement buyer item bid count
				String successfulBidder = highestBidders.get(listingID);
				itemsPerBuyer.put(successfulBidder, itemsPerBuyer.get(successfulBidder) - 1);

				// if bid won by current chap, return success
				if (highestBidders.get(listingID).equals(bidderName)) {
					//System.out.println("Firing for incrementing sold & revenue for item id " + listingID + " for " + bidderName );

					// increase sold item count
					incSoldItems();
					//System.out.println("Item count recorded " + soldItemsCount);

					// increase revenue
					incRevenue(highestBids.get(listingID));

					return 1;
				} else {
					// if not, return failure
					return 3;
				}
			} else {
				// if open, do nothing
				return 2;
			}
		}
		return 3;
	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	public int itemPrice(int listingID) {
		// See if the item exists.
		if (itemsAndIDs.containsKey(listingID)) {
			Item item = itemsAndIDs.get(listingID);

			// Getting proper o/p even without this lock however, when used, performance is better and hence using
			// Get lowest price info
			int currPrice = item.lowestBiddingPrice();

			// If item is already bid upon, get highest price
			if (highestBids.containsKey(listingID)) {
				currPrice = highestBids.get(listingID);
			}
			return currPrice;
		}

		return -1;
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */
	public Boolean itemUnbid(int listingID) {
		// See if the item exists.
		if(itemsAndIDs.containsKey(listingID)){
			// If item is already bid upon, return false
			if(highestBids.containsKey(listingID)){
				return false;
			}
			return true;
		}

		return true;
	}


	public ConcurrentHashMap<Integer,String>getHighestBidders(){
		return highestBidders;
	}

	public ConcurrentHashMap<Integer,Integer> getHighestBids(){
		return highestBids;
	}

	public ConcurrentHashMap<Integer,Item> getSubmittedItems(){
		return itemsAndIDs;
	}


	public void updateHighestBidder(String bidderName){

	}

	public int incLastListingID() {
		boolean updated = false;
		int newCount = 0;
		while(!updated){
			int prevCount = this.lastListingID.get();
			newCount = prevCount + 1;
			updated = this.lastListingID.compareAndSet(prevCount, newCount);
		}

		return newCount;
	}

	public void incCount() {
		boolean updated = false;
		while(!updated){
			int prevCount = this.count.get();
			updated = this.count.compareAndSet(prevCount, prevCount + 1);
		}
	}

	public void incRevenue(int highestBid) {
		boolean updated = false;
		while(!updated){
			int prevCount = this.revenue.get();
			updated = this.revenue.compareAndSet(prevCount, prevCount + highestBid);
		}
	}

	public void incSoldItems() {
		boolean updated = false;
		while(!updated){
			int prevCount = this.soldItemsCount.get();
			updated = this.soldItemsCount.compareAndSet(prevCount, prevCount + 1);
		}
	}

}
 