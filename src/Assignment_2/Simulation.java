package Assignment_2;


/**
 * Class provided for ease of test. This will not be used in the project 
 * evaluation, so feel free to modify it as you like.
 */

// for starters, create 50 seller threads and 20 bidder threads
public class Simulation {
    public static void main(String[] args) {

        /* BASIC TESTING */
        int nrSellers = 50; //Orig : 50
        int nrBidders = 20; //Orig : 20
        
        Thread[] sellerThreads = new Thread[nrSellers]; //create 50 seller thread objects
        Thread[] bidderThreads = new Thread[nrBidders]; //create 20 bidder thread objects
        Seller[] sellers = new Seller[nrSellers]; //create 50 seller objects
        Bidder[] bidders = new Bidder[nrBidders]; //create 20 bidder objects
        
        // Run this for 50 times and create 50 different threads
        for (int i=0; i < nrSellers; ++i) {
            // for each seller
            sellers[i] = new Seller(
                                        AuctionServer.getInstance(), //get singleton instance
                                        "Seller"+i, // provide name to this seller object
                                        100, // specify no of attempts for which it should try to list an item //100
                                        50, // specify max sleep time (ms) between diff attempts
                                        i // no use of this - just gives a starting point for generating random num
                                    );
            // create a thread of this object
            sellerThreads[i] = new Thread(sellers[i]);

            // spin out a thread
            sellerThreads[i].start();
        }
        
        // Run this for 20 times and create 20 different threads
        for (int i=0; i<nrBidders; ++i)        {
            bidders[i] = new Bidder(
                                        AuctionServer.getInstance(), //get singleton instance
                                        "Buyer"+i,  // provide name to this buyer object
                                        1000, // provide cash for purchase
                                        20, // specify no of attempts for which it should try to buy an item
                                        150, // specify max sleep time (ms) between diff attempts
                                        i  // no use of this - just gives a starting point for generating random num
                                    );
            // create a thread of this object
            bidderThreads[i] = new Thread(bidders[i]);

            // spin out a thread
            bidderThreads[i].start();
        }
        
        // Let all seller thread finish executing
        for (int i=0; i<nrSellers; ++i) {
            try{
                sellerThreads[i].join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Let all bidder thread finish executing
        for (int i=0; i<nrBidders; ++i){
            try{
                bidderThreads[i].join();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        AuctionServer server = AuctionServer.getInstance();
        int soldItems = server.soldItemsCount();
        int revenue = server.revenue();

        int soldItemsRecorded =  server.getHighestBidders().size();

        int revenueRecorded = 0;
        for(int value : server.getHighestBids().values()){
            revenueRecorded = revenueRecorded + value;
        }

        System.out.println("Sold item count recorded " + soldItemsRecorded +  " and actual sold items " + soldItems);
        System.out.println("Revnue recorded " + revenueRecorded +  " and actual revenue found " + revenue);



        /* ------------------------------------ Different Testing                 -------------- */
        System.out.println("\n TESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSTING \n");
        System.out.println(" /* ------------------------------------ Different Testing                 -------------- */");
        System.out.println(" /* ------------------------------------ Different Testing                 -------------- */");
        System.out.println(" /* ------------------------------------ Different Testing                 -------------- */");
        System.out.println(" /* ------------------------------------ Different Testing                 -------------- */");
        System.out.println(" /* ------------------------------------ Different Testing                 -------------- */");
        System.out.println(" /* ------------------------------------ Different Testing                 -------------- */");
        System.out.println(" /* ------------------------------------ Different Testing                 -------------- */");
        System.out.println(" /* ------------------------------------ Different Testing                 -------------- */");
        System.out.println("\n TESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSTING \n");

        nrSellers = 10; //Orig : 50
        nrBidders = 10; //Orig : 20

        // Run this for 50 times and create 50 different threads
        for (int i=0; i < nrSellers; ++i) {
            // for each seller
            sellers[i] = new Seller(
                    AuctionServer.getInstance(), //get singleton instance
                    "Seller"+i, // provide name to this seller object
                    100, // specify no of attempts for which it should try to list an item //100
                    50, // specify max sleep time (ms) between diff attempts
                    i // no use of this - just gives a starting point for generating random num
            );
            bidders[i] = new Bidder(
                    AuctionServer.getInstance(), //get singleton instance
                    "Buyer"+i,  // provide name to this buyer object
                    1000, // provide cash for purchase
                    20, // specify no of attempts for which it should try to buy an item
                    150, // specify max sleep time (ms) between diff attempts
                    i  // no use of this - just gives a starting point for generating random num
            );


            // create a thread of this object
            sellerThreads[i] = new Thread(sellers[i]);
            // create a thread of this object
            bidderThreads[i] = new Thread(bidders[i]);

            // spin out a thread
            sellerThreads[i].start();

            // spin out a thread
            bidderThreads[i].start();
        }

        // Let all seller thread finish executing
        for (int i=0; i<nrSellers; ++i) {
            try{
                sellerThreads[i].join();
                bidderThreads[i].join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        server = AuctionServer.getInstance();
        soldItems = server.soldItemsCount();
        revenue = server.revenue();

        soldItemsRecorded =  server.getHighestBidders().size();

        revenueRecorded = 0;
        for(int value : server.getHighestBids().values()){
            revenueRecorded = revenueRecorded + value;
        }

        System.out.println("Sold item count recorded " + soldItemsRecorded +  " and actual sold items " + soldItems);
        System.out.println("Revnue recorded " + revenueRecorded +  " and actual revenue found " + revenue);
    }
}