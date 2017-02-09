package Assignment_2;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class Bidder implements Client {

    private String name;
    private int cash;
    private int cycles;
    private int maxSleepTimeMs;
    private int initialCash;
    private Random rand;
    private AuctionServer server;
    private int mostItemsAvailable = 0;

    public Bidder(AuctionServer server, String name, int cash, int cycles, int maxSleepTimeMs, long randomSeed) {
        //take name
        this.name = name;

        // set spending limit
        this.cash = cash;

        // assign total attempts allowed which is 20
        this.cycles = cycles;

        // max sleep time between attempts which is 150ms
        this.maxSleepTimeMs = maxSleepTimeMs;

        // init cash is same as cash provided for spending
        this.initialCash = cash;

        // what? why? moving on..
        this.rand = new Random(randomSeed);

        // auction fellow
        this.server = server;
    }

    // get current cash you have
    public int cash() {
        return this.cash;
    }

    // how much is spent so far? get that
    public int cashSpent() {
        return this.initialCash - this.cash;
    }

    // size of menu
    public int mostItemsAvailable() {
        return this.mostItemsAvailable;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void run() {
        // data store for active bids (catalogue, basically menu card)
        List<Item> activeBids = new ArrayList<Item>();

        // list of current bid applied on items
        Hashtable<Item, Integer> activeBidPrices = new Hashtable<Item, Integer>();

        // sum of total money on the line so far
        int sumActiveBids = 0;

        // if attempt count < 20 and cash available > 0 and menu has entries
        for (int i = 0; (i < cycles && cash > 0) || activeBids.size() > 0; ++i) {
            // get menu and store
            List<Item> items = server.getItems();

            // set size - count of total items on menu
            if (items.size() > this.mostItemsAvailable) {
                this.mostItemsAvailable = items.size();
            }

            // if no of items in menu are > 0
            while (items.size() > 0) {
                // select anyone
                int index = rand.nextInt(items.size());

                // get selected item
                Item item = items.get(index);

                // remove from menu
                items.remove(index);

                // get price of selected item
                int price = server.itemPrice(item.listingID());

                // if price is less than cash available
                if (price < this.cash - sumActiveBids) {
                    // The server should ensure thread safety: if the price
                    // has already increased, then this bid should be invalid.

                    //if within reach, bleedy bid yaar (provide bidderName, listingId & currBidPrice+1)
                    boolean success = server.submitBid(this.name(), item.listingID(), price + 1);

                    if (success) {
                        // if successful, add to currActiveBids
                        if (!activeBidPrices.containsKey(item)) {
                            activeBids.add(item);
                        } else {
                            // if not, take thy money back from the pot
                            sumActiveBids -= activeBidPrices.get(item);
                        }

                        // add bid to sum
                        sumActiveBids += price + 1;

                        // update list with item and new price
                        activeBidPrices.put(item, price + 1);
                    }

                    //gimme a break
                    break;
                }

                // if price is more than cash available, let him try one more time, maybe it can get something cheaper next time, no?
                continue;
            }

            // time for fact checking
            List<Item> newActiveBids = new ArrayList<Item>();
            Hashtable<Item, Integer> newActiveBidPrices = new Hashtable<Item, Integer>();

            // get all activeBids for this bidder
            for (Item bid : activeBids) {
                // check status of bid
                switch (server.checkBidStatus(this.name(), bid.listingID())) {
                    case 1:
                        // Success

                        // if bid accepted, remove price from cash and sumActiveBids
                        int finalPrice = activeBidPrices.get(bid);
                        this.cash -= finalPrice;
                        sumActiveBids -= finalPrice;

                        break;

                    case 2:
                        // Open
                        // if open get latest price
                        newActiveBids.add(bid);
                        newActiveBidPrices.put(bid, activeBidPrices.get(bid));
                        break;

                    case 3:
                        // Failed
                        // if failed, show me the money!
                        sumActiveBids -= activeBidPrices.get(bid);
                        break;

                    default:
                        // Error
                        break;
                }
            }

            // renewed active bids
            activeBids = newActiveBids;

            // renewed prices
            activeBidPrices = newActiveBidPrices;
            
            try {
                Thread.sleep((long)rand.nextInt(this.maxSleepTimeMs));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}