package AdvancedLocking;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLock {
    public static final int HIGHEST_PRICE = 1000;

    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();

        Random random = new Random();
        // add 100,000 items with a random positive number with a ceiling of HIGHEST_PRICE == 1000.
        for (int i = 0; i < 100000; i++) {
            inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        }
        // create a writer thread that does the add/remove operations
        Thread writer = new Thread(() -> {
            while (true) {
                inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
                inventoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        });
        // create a Daemon writer thread and start the thread
        writer.setDaemon(true);
        writer.start();

        // assign the total number of reader threads
        int numberOfReaderThreads = 7;
        // store the read threads in a List
        List<Thread> readers = new ArrayList<>();

        for (int readerIndex = 0; readerIndex < numberOfReaderThreads; readerIndex++) {
            // create 'numberOfReaderThreads' threads that do the query job
            Thread reader = new Thread(() -> {
                for (int i = 0; i < 100000; i++) {
                    int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                    inventoryDatabase.getNumberOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
                }
            });

            reader.setDaemon(true);
            readers.add(reader);
        }
        // record the current system time
        long startReadingTime = System.currentTimeMillis();
        // start the reader threads sequentially and let it run in parallel
        for (Thread reader : readers) {
            reader.start();
        }
        // let the reader threads join when their jobs are done
        for (Thread reader : readers) {
            reader.join();
        }
        // record the finishing time
        long endReadingTime = System.currentTimeMillis();

        // print out the final running time result
        System.out.println(String.format("Reading took %d ms", endReadingTime - startReadingTime));
    }

    public static class InventoryDatabase {
        // create a TreeMap that stores the Price-Amount information in Key-Value pairs
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        // create a reentrant read-write lock
        private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        // create the read lock
        private Lock readLock = reentrantReadWriteLock.readLock();
        // create the write lock
        private Lock writeLock = reentrantReadWriteLock.writeLock();
        // also create a normal Reentrant lock for the performance comparison
        private Lock lock = new ReentrantLock();

        public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
            //lock.lock();
            readLock.lock();
            try {
                // get the lowest price
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                // get the highest price
                Integer toKey = priceToCountMap.floorKey(upperBound);

                if (fromKey == null || toKey == null) {
                    return 0;
                }
                // get a snapshot the the tree that is within the price range
                NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);
                // sum up the total number of the items of the tree that are within the price range
                int sum = 0;
                for (int numberOfItemsForPrice : rangeOfPrices.values()) {
                    sum += numberOfItemsForPrice;
                }

                return sum;
            } finally {
                readLock.unlock();
                //lock.unlock();
            }
        }

        public void addItem(int price) {
            //lock.lock();
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceToCountMap.get(price);
                if (numberOfItemsForPrice == null) {
                    priceToCountMap.put(price, 1);
                } else {
                    priceToCountMap.put(price, numberOfItemsForPrice + 1);
                }

            } finally {
                writeLock.unlock();
                /// lock.unlock();
            }
        }

        public void removeItem(int price) {
            //lock.lock();
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceToCountMap.get(price);
                if (numberOfItemsForPrice == null || numberOfItemsForPrice == 1) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, numberOfItemsForPrice - 1);
                }
            } finally {
                writeLock.unlock();
                // lock.unlock();
            }
        }
    }
}
