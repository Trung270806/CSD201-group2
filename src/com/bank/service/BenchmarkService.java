package com.bank.service;

import com.bank.datastructure.CustomHashTable;
import com.bank.datastructure.CustomSinglyLinkedList;
import com.bank.model.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class BenchmarkService {

    public static class BenchmarkResult {
        public int datasetSize;
        public int lookupCount;
        
        public long hashInsertTimeNs;
        public long listInsertTimeNs;
        
        public long hashSearchTimeNs;
        public long listSearchTimeNs;

        public double searchSpeedupRatio;
    }

    /**
     * Runs a performance test comparing the custom Hash Table and Singly Linked List.
     *
     * @param transactionsPath the path to the transactions CSV file to read.
     * @param lookupCount the number of random lookups to perform.
     * @return the result of the benchmark or null if error.
     */
    public static BenchmarkResult runBenchmark(String transactionsPath, int lookupCount) {
        System.out.println("Starting Benchmark...");
        System.out.println("Reading transactions from: " + transactionsPath);

        CustomHashTable hashTable = new CustomHashTable();
        CustomSinglyLinkedList linkedList = new CustomSinglyLinkedList();

        // 1. Ingest Data & Measure Insert Time
        long hashInsertStart = System.nanoTime();
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(transactionsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction tx = Transaction.fromCSV(line);
                if (tx != null) {
                    hashTable.put(tx.getId(), tx);
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transactions during benchmark load (Hash Table): " + e.getMessage());
            return null;
        }
        long hashInsertEnd = System.nanoTime();

        long listInsertStart = System.nanoTime();
        try (BufferedReader reader = new BufferedReader(new FileReader(transactionsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction tx = Transaction.fromCSV(line);
                if (tx != null) {
                    linkedList.add(tx);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transactions during benchmark load (Linked List): " + e.getMessage());
            return null;
        }
        long listInsertEnd = System.nanoTime();

        if (count == 0) {
            System.out.println("No transactions found to benchmark. Please generate data first.");
            return null;
        }

        System.out.println("Loaded " + count + " records into both structures.");

        // 2. Select IDs to Search For
        // Retrieve all transactions from the hash table to get valid search keys
        Transaction[] allTx = hashTable.getAll();
        String[] searchIds = new String[lookupCount];
        Random rand = new Random();
        for (int i = 0; i < lookupCount; i++) {
            searchIds[i] = allTx[rand.nextInt(allTx.length)].getId();
        }

        // 3. Measure Custom Hash Table search time
        long hashSearchStart = System.nanoTime();
        int hashHits = 0;
        for (String id : searchIds) {
            Transaction tx = hashTable.get(id);
            if (tx != null) {
                hashHits++;
            }
        }
        long hashSearchEnd = System.nanoTime();

        // 4. Measure Custom Singly Linked List search time
        long listSearchStart = System.nanoTime();
        int listHits = 0;
        for (String id : searchIds) {
            Transaction tx = linkedList.find(id);
            if (tx != null) {
                listHits++;
            }
        }
        long listSearchEnd = System.nanoTime();

        // 5. Compile Results
        BenchmarkResult res = new BenchmarkResult();
        res.datasetSize = count;
        res.lookupCount = lookupCount;
        
        res.hashInsertTimeNs = hashInsertEnd - hashInsertStart;
        res.listInsertTimeNs = listInsertEnd - listInsertStart;
        
        res.hashSearchTimeNs = hashSearchEnd - hashSearchStart;
        res.listSearchTimeNs = listSearchEnd - listSearchStart;

        res.searchSpeedupRatio = (double) res.listSearchTimeNs / Math.max(1, res.hashSearchTimeNs);

        return res;
    }

    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir");
        String transactionsPath = currentDir + "/data/transactions.csv";
        int lookupCount = 1000;
        
        System.out.println("Running direct benchmark of 1,000 lookups...");
        BenchmarkResult res = runBenchmark(transactionsPath, lookupCount);
        if (res != null) {
            System.out.println("====================================================");
            System.out.println("               BENCHMARK RESULTS                    ");
            System.out.println("====================================================");
            System.out.printf("Dataset Size (N):     %,d transactions\n", res.datasetSize);
            System.out.printf("Number of Lookups:    %,d lookups\n", res.lookupCount);
            System.out.printf("Hash Table Search:    %,d ns (avg %,.1f ns/lookup)\n", res.hashSearchTimeNs, (double) res.hashSearchTimeNs / res.lookupCount);
            System.out.printf("Singly List Search:   %,d ns (avg %,.1f ns/lookup)\n", res.listSearchTimeNs, (double) res.listSearchTimeNs / res.lookupCount);
            System.out.printf("Speedup Ratio:        %.2fx FASTER\n", res.searchSpeedupRatio);
            System.out.println("====================================================");
        } else {
            System.err.println("Benchmark failed.");
        }
    }
}
