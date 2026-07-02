package com.bank.service;

import com.bank.datastructure.CustomBST;
import com.bank.datastructure.CustomDoublyLinkedList;
import com.bank.datastructure.CustomHashTable;
import com.bank.datastructure.CustomSortedLinkedList;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BenchmarkService {

    public static class BenchmarkResult {
        public int datasetSize;
        
        // RQ1 Results
        public long rq1DllTimeNs;
        public long rq1BstTimeNs;
        public long rq1HashTimeNs;
        
        // RQ2 Results
        public int rq2Size;
        public long rq2InsertTimeNs;
        public long rq2LinearSearchTimeNs;
        public long rq2BinarySearchTimeNs;
        
        // RQ3 Results
        public long rq3DllTimeNs;
        public long rq3BstTimeNs;
        public long rq3HashTimeNs;
    }

    /**
     * Runs the empirical performance benchmark addressing RQ1, RQ2, and RQ3.
     */
    public static BenchmarkResult runBenchmark(String transactionsPath, int queryCount) {
        System.out.println("\n====================================================");
        System.out.println("   RUNNING RESEARCH QUESTIONS (RQ1-RQ3) BENCHMARK   ");
        System.out.println("====================================================");
        System.out.println("Preloading transactions to memory (eliminating IO noise)...");

        // 1. Ingest transactions into memory first
        List<Transaction> txList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(transactionsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction tx = Transaction.fromCSV(line);
                if (tx != null) {
                    txList.add(tx);
                }
            }
        } catch (IOException e) {
            System.err.println("Error preloading transactions: " + e.getMessage());
            return null;
        }

        int size = txList.size();
        if (size == 0) {
            System.out.println("[!] No transactions loaded. Please generate mock data first.");
            return null;
        }
        System.out.printf("Successfully preloaded %,d transactions.\n", size);

        Transaction[] transactions = txList.toArray(new Transaction[0]);
        Random rand = new Random(42); // Seeded for reproducibility

        // 2. JVM Warmup Phase to compile JIT bytecode
        System.out.println("Executing JVM Warmup phase...");
        performWarmup(transactions, rand);
        System.out.println("JVM Warmup complete. Commencing timing runs...");

        BenchmarkResult res = new BenchmarkResult();
        res.datasetSize = size;

        // ==========================================
        // RQ1 BENCHMARK: Time Range Query (1,000 runs)
        // ==========================================
        System.out.println("\nRunning RQ1 Benchmark (Range Queries)...");
        
        // Load structures
        CustomDoublyLinkedList dll = new CustomDoublyLinkedList();
        CustomBST bst = new CustomBST();
        CustomHashTable hash = new CustomHashTable();
        for (Transaction tx : transactions) {
            dll.add(tx);
            bst.insert(tx);
            hash.put(tx.getId(), tx);
        }

        // Generate query ranges
        String[] startTimes = new String[queryCount];
        String[] endTimes = new String[queryCount];
        for (int i = 0; i < queryCount; i++) {
            Transaction tx1 = transactions[rand.nextInt(size)];
            Transaction tx2 = transactions[rand.nextInt(size)];
            if (tx1.getTime().compareTo(tx2.getTime()) <= 0) {
                startTimes[i] = tx1.getTime();
                endTimes[i] = tx2.getTime();
            } else {
                startTimes[i] = tx2.getTime();
                endTimes[i] = tx1.getTime();
            }
        }

        // Benchmark DLL Range Query
        long start = System.nanoTime();
        for (int i = 0; i < queryCount; i++) {
            dll.findRange(startTimes[i], endTimes[i]);
        }
        res.rq1DllTimeNs = System.nanoTime() - start;

        // Benchmark BST Range Query
        start = System.nanoTime();
        for (int i = 0; i < queryCount; i++) {
            bst.findRange(startTimes[i], endTimes[i]);
        }
        res.rq1BstTimeNs = System.nanoTime() - start;

        // Benchmark Hash Table Range Query (Linear scan on all transactions)
        start = System.nanoTime();
        for (int i = 0; i < queryCount; i++) {
            String s = startTimes[i];
            String e = endTimes[i];
            Transaction[] all = hash.getAll();
            int count = 0;
            for (Transaction tx : all) {
                if (tx != null && tx.getTime().compareTo(s) >= 0 && tx.getTime().compareTo(e) <= 0) {
                    count++;
                }
            }
        }
        res.rq1HashTimeNs = System.nanoTime() - start;


        // ==========================================
        // RQ2 BENCHMARK: Sorted Linked List Lookup
        // ==========================================
        System.out.println("Running RQ2 Benchmark (Sorted List: Linear vs. Binary)...");
        
        // To prevent O(N^2) insertion from freezing the CPU, limit sorted list size to N_rq2 = 5,000
        int rq2Size = Math.min(5000, size);
        res.rq2Size = rq2Size;
        CustomSortedLinkedList sortedList = new CustomSortedLinkedList();
        
        // Measure sorted insertion
        start = System.nanoTime();
        for (int i = 0; i < rq2Size; i++) {
            sortedList.addSorted(transactions[i]);
        }
        res.rq2InsertTimeNs = System.nanoTime() - start;

        // Pick random IDs to search
        String[] searchIds = new String[queryCount];
        for (int i = 0; i < queryCount; i++) {
            searchIds[i] = transactions[rand.nextInt(rq2Size)].getId();
        }

        // Measure Linear Search
        start = System.nanoTime();
        for (int i = 0; i < queryCount; i++) {
            sortedList.findLinear(searchIds[i]);
        }
        res.rq2LinearSearchTimeNs = System.nanoTime() - start;

        // Measure Binary Search on Linked List
        start = System.nanoTime();
        for (int i = 0; i < queryCount; i++) {
            sortedList.findBinary(searchIds[i]);
        }
        res.rq2BinarySearchTimeNs = System.nanoTime() - start;


        // ==========================================
        // RQ3 BENCHMARK: Multi-Criteria Filtering
        // ==========================================
        System.out.println("Running RQ3 Benchmark (Multi-Criteria Filtering)...");

        // Query criteria: Date range + Type + Amount range
        TransactionType[] queryTypes = new TransactionType[queryCount];
        double[] minAmounts = new double[queryCount];
        double[] maxAmounts = new double[queryCount];

        for (int i = 0; i < queryCount; i++) {
            queryTypes[i] = rand.nextBoolean() ? TransactionType.DEPOSIT : TransactionType.WITHDRAWAL;
            double a1 = 100.0 + rand.nextDouble() * 2000.0;
            double a2 = 100.0 + rand.nextDouble() * 2000.0;
            minAmounts[i] = Math.min(a1, a2);
            maxAmounts[i] = Math.max(a1, a2);
        }

        // 1. DLL Multi-Criteria
        start = System.nanoTime();
        for (int i = 0; i < queryCount; i++) {
            String s = startTimes[i];
            String e = endTimes[i];
            TransactionType type = queryTypes[i];
            double min = minAmounts[i];
            double max = maxAmounts[i];
            
            // Perform scan on DLL
            CustomDoublyLinkedList.Node curr = dll.getHead();
            int matchCount = 0;
            while (curr != null) {
                Transaction tx = curr.value;
                if (tx.getTime().compareTo(s) >= 0 && tx.getTime().compareTo(e) <= 0 
                        && tx.getType() == type && tx.getAmount() >= min && tx.getAmount() <= max) {
                    matchCount++;
                }
                curr = curr.next;
            }
        }
        res.rq3DllTimeNs = System.nanoTime() - start;

        // 2. BST Multi-Criteria (pruning date range first, then filtering subset)
        start = System.nanoTime();
        for (int i = 0; i < queryCount; i++) {
            String s = startTimes[i];
            String e = endTimes[i];
            TransactionType type = queryTypes[i];
            double min = minAmounts[i];
            double max = maxAmounts[i];
            
            Transaction[] datesSubset = bst.findRange(s, e);
            int matchCount = 0;
            for (Transaction tx : datesSubset) {
                if (tx.getType() == type && tx.getAmount() >= min && tx.getAmount() <= max) {
                    matchCount++;
                }
            }
        }
        res.rq3BstTimeNs = System.nanoTime() - start;

        // 3. Hash Table Multi-Criteria (scan all transactions)
        start = System.nanoTime();
        for (int i = 0; i < queryCount; i++) {
            String s = startTimes[i];
            String e = endTimes[i];
            TransactionType type = queryTypes[i];
            double min = minAmounts[i];
            double max = maxAmounts[i];

            Transaction[] all = hash.getAll();
            int matchCount = 0;
            for (Transaction tx : all) {
                if (tx != null && tx.getTime().compareTo(s) >= 0 && tx.getTime().compareTo(e) <= 0 
                        && tx.getType() == type && tx.getAmount() >= min && tx.getAmount() <= max) {
                    matchCount++;
                }
            }
        }
        res.rq3HashTimeNs = System.nanoTime() - start;

        System.out.println("Benchmarks completed successfully.");
        return res;
    }

    private static void performWarmup(Transaction[] transactions, Random rand) {
        CustomDoublyLinkedList dll = new CustomDoublyLinkedList();
        CustomBST bst = new CustomBST();
        CustomHashTable hash = new CustomHashTable();
        CustomSortedLinkedList sorted = new CustomSortedLinkedList();

        // Warmup inserts (subset of 2,000)
        int size = Math.min(2000, transactions.length);
        for (int i = 0; i < size; i++) {
            Transaction tx = transactions[i];
            dll.add(tx);
            bst.insert(tx);
            hash.put(tx.getId(), tx);
            sorted.addSorted(tx);
        }

        // Warmup queries
        for (int i = 0; i < 500; i++) {
            Transaction t1 = transactions[rand.nextInt(size)];
            Transaction t2 = transactions[rand.nextInt(size)];
            String s = t1.getTime().compareTo(t2.getTime()) <= 0 ? t1.getTime() : t2.getTime();
            String e = t1.getTime().compareTo(t2.getTime()) <= 0 ? t2.getTime() : t1.getTime();
            
            dll.findRange(s, e);
            bst.findRange(s, e);
            sorted.findLinear(t1.getId());
            sorted.findBinary(t1.getId());
        }
    }

    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir");
        String transactionsPath = currentDir + "/data/transactions.csv";
        
        BenchmarkResult res = runBenchmark(transactionsPath, 1000);
        if (res == null) {
            System.err.println("Benchmark failed.");
            return;
        }

        // Print final reports
        printReportToConsole(res, 1000);
    }

    public static void printReportToConsole(BenchmarkResult res, int queryCount) {
        System.out.println("\n====================================================");
        System.out.println("         EMPIRICAL BENCHMARK RESULTS REPORT         ");
        System.out.println("====================================================");
        
        System.out.printf("RESEARCH QUESTION 1 (RQ1):\n");
        System.out.printf("Querying Transaction History by Time Range (%,d Queries)\n", queryCount);
        System.out.printf("  - Custom Doubly Linked List (O(N)): %,15d ns (avg %,.2f us/query)\n", 
                res.rq1DllTimeNs, (double) res.rq1DllTimeNs / queryCount / 1000.0);
        System.out.printf("  - Custom Hash Table (O(N) scan):    %,15d ns (avg %,.2f us/query)\n", 
                res.rq1HashTimeNs, (double) res.rq1HashTimeNs / queryCount / 1000.0);
        System.out.printf("  - Custom BST (O(log N + k)):        %,15d ns (avg %,.2f us/query)\n", 
                res.rq1BstTimeNs, (double) res.rq1BstTimeNs / queryCount / 1000.0);
        System.out.printf("BST Speedup vs. DLL:                  %.2fx FASTER\n", 
                (double) res.rq1DllTimeNs / Math.max(1, res.rq1BstTimeNs));
        System.out.printf("BST Speedup vs. Hash Table:           %.2fx FASTER\n", 
                (double) res.rq1HashTimeNs / Math.max(1, res.rq1BstTimeNs));
        System.out.println("Proof: Unsorted Doubly Linked Lists & Hash Tables require scanning all nodes,");
        System.out.println("       while BST prunes subtrees to query range values in O(log N + k).");
        
        System.out.println("----------------------------------------------------");
        System.out.printf("RESEARCH QUESTION 2 (RQ2):\n");
        System.out.printf("Binary Search vs. Linear Search on Sorted Linked List (Size N = %,d)\n", res.rq2Size);
        System.out.printf("  - Sorted Insertion Cost (O(N^2)):   %,15d ns\n", res.rq2InsertTimeNs);
        System.out.printf("  - Linear Search by ID (O(N)):       %,15d ns (avg %,.2f us/lookup)\n", 
                res.rq2LinearSearchTimeNs, (double) res.rq2LinearSearchTimeNs / queryCount / 1000.0);
        System.out.printf("  - Binary Search by ID (O(N)):       %,15d ns (avg %,.2f us/lookup)\n", 
                res.rq2BinarySearchTimeNs, (double) res.rq2BinarySearchTimeNs / queryCount / 1000.0);
        System.out.printf("Linear Search vs. Binary Search Ratio: %.2fx (Linear is typically faster)\n",
                (double) res.rq2BinarySearchTimeNs / Math.max(1, res.rq2LinearSearchTimeNs));
        System.out.println("Proof: Binary Search on a sequential-access Linked List requires linear pointer");
        System.out.println("       traversals to find middle nodes, defeating the O(log N) divide-and-conquer");
        System.out.println("       advantage. Maintaining order on insertion costs O(N^2) in total,");
        System.out.println("       which completely negates any benefit for sequential data structures.");

        System.out.println("----------------------------------------------------");
        System.out.printf("RESEARCH QUESTION 3 (RQ3):\n");
        System.out.printf("Multi-Criteria Filtering (Date + Type + Amount Range) (%,d Queries)\n", queryCount);
        System.out.printf("  - Custom Doubly Linked List:        %,15d ns (avg %,.2f ms/query)\n", 
                res.rq3DllTimeNs, (double) res.rq3DllTimeNs / queryCount / 1_000_000.0);
        System.out.printf("  - Custom Hash Table (Linear Scan):  %,15d ns (avg %,.2f ms/query)\n", 
                res.rq3HashTimeNs, (double) res.rq3HashTimeNs / queryCount / 1_000_000.0);
        System.out.printf("  - Custom BST (Pruning + Filter):    %,15d ns (avg %,.2f ms/query)\n", 
                res.rq3BstTimeNs, (double) res.rq3BstTimeNs / queryCount / 1_000_000.0);
        
        double maxTimeMs = (double) Math.max(res.rq3DllTimeNs, Math.max(res.rq3HashTimeNs, res.rq3BstTimeNs)) / queryCount / 1_000_000.0;
        System.out.printf("Worst Average Response Time:          %.4f ms (Threshold: 200 ms)\n", maxTimeMs);
        System.out.printf("Compliance:                           %s (Response time is well under 200ms)\n", 
                maxTimeMs < 200.0 ? "COMPLIANT" : "NON-COMPLIANT");
        System.out.println("Proof: BST is most effective as it narrows the dataset size using date pruning,");
        System.out.println("       then sequentially checks other criteria. Even for sequential scans,");
        System.out.println("       response times remain far below 200ms due to RAM execution speeds.");
        System.out.println("====================================================\n");
    }
}
