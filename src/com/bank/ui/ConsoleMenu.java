package com.bank.ui;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.service.BankService;
import com.bank.service.BenchmarkService;
import com.bank.service.DataGenerator;

import java.util.Scanner;

public class ConsoleMenu {
    private final BankService bankService;
    private final String accountsPath;
    private final String transactionsPath;
    private final Scanner scanner;

    public ConsoleMenu(BankService bankService, String accountsPath, String transactionsPath) {
        this.bankService = bankService;
        this.accountsPath = accountsPath;
        this.transactionsPath = transactionsPath;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        // Load initial data
        System.out.println("====================================================");
        System.out.println("  BANK TRANSACTION HISTORY SYSTEM (CSD201 PROJECT)   ");
        System.out.println("====================================================");
        System.out.println("Loading ledger data...");
        bankService.loadFromCSV(accountsPath, transactionsPath);
        System.out.println("Ledger initialized successfully.");
        System.out.println("Active Accounts: " + bankService.getAccounts().length);
        System.out.println("Total Transactions Loaded: " + bankService.getAllTransactions().length);

        boolean running = true;
        while (running) {
            printMenuHeader();
            int choice = readIntInput("Select an option (1-9): ");
            switch (choice) {
                case 1:
                    viewAccounts();
                    break;
                case 2:
                    addNewAccount();
                    break;
                case 3:
                    postNewTransaction();
                    break;
                case 4:
                    reverseTransaction();
                    break;
                case 5:
                    searchTransaction();
                    break;
                case 6:
                    viewAnalyticsDashboard();
                    break;
                case 7:
                    generateSimulationData();
                    break;
                case 8:
                    runPerformanceBenchmark();
                    break;
                case 9:
                    saveAndExit();
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please choose a value from 1 to 9.");
            }
        }
    }

    private void printMenuHeader() {
        System.out.println("\n====================================================");
        System.out.println("                 CLI CONTROL PANEL                  ");
        System.out.println("====================================================");
        System.out.println("1. View Accounts & Balances");
        System.out.println("2. Add New Account");
        System.out.println("3. Post New Transaction (Deposit/Withdrawal)");
        System.out.println("4. Process Reversal Transaction (Undo alternative)");
        System.out.println("5. Fast Lookup by Transaction ID [O(1) Hash Table]");
        System.out.println("6. View 12-Month Cash Flow Analytics Dashboard");
        System.out.println("7. Generate Simulation Dataset (N >= 10,000)");
        System.out.println("8. Run Performance Benchmark (Hash Table vs. Linked List)");
        System.out.println("9. Save Ledger and Exit");
        System.out.println("====================================================");
    }

    private void viewAccounts() {
        System.out.println("\n--- [1] ACTIVE BANK ACCOUNTS ---");
        Account[] list = bankService.getAccounts();
        if (list.length == 0) {
            System.out.println("No accounts found. Use Option 2 to create one, or Option 7 to generate mock data.");
            return;
        }
        System.out.printf("%-15s | %-20s\n", "Account Number", "Current Balance (VND)");
        System.out.println("----------------------------------------");
        for (Account acc : list) {
            System.out.printf("%-15s | %-20.2f\n", acc.getAccountNumber(), acc.getBalance());
        }
    }

    private void addNewAccount() {
        System.out.println("\n--- [2] ADD NEW ACCOUNT ---");
        String accNum = readStringInput("Enter new account number (e.g. ACC000001): ").trim();
        if (accNum.isEmpty()) {
            System.out.println("[!] Account number cannot be empty.");
            return;
        }
        double balance = readDoubleInput("Enter initial balance: ");
        if (balance < 0) {
            System.out.println("[!] Initial balance cannot be negative.");
            return;
        }

        boolean success = bankService.addAccount(accNum, balance);
        if (success) {
            System.out.printf("[✓] Account %s successfully created.\n", accNum);
        } else {
            System.out.println("[!] Failed to create account. It might already exist or system is at capacity.");
        }
    }

    private void postNewTransaction() {
        System.out.println("\n--- [3] POST NEW TRANSACTION ---");
        String accNum = readStringInput("Enter account number: ").trim();
        Account acc = bankService.findAccount(accNum);
        if (acc == null) {
            System.out.println("[!] Account not found.");
            return;
        }
        System.out.println("Current Account Balance: " + acc.getBalance());
        System.out.println("Select Type: 1. DEPOSIT | 2. WITHDRAWAL");
        int typeChoice = readIntInput("Your choice: ");
        TransactionType type;
        if (typeChoice == 1) {
            type = TransactionType.DEPOSIT;
        } else if (typeChoice == 2) {
            type = TransactionType.WITHDRAWAL;
        } else {
            System.out.println("[!] Invalid type selection.");
            return;
        }

        double amount = readDoubleInput("Enter transaction amount: ");
        String result = bankService.postTransaction(accNum, amount, type);
        if (result.startsWith("SUCCESS")) {
            System.out.println("[✓] " + result);
            System.out.println("New Account Balance: " + acc.getBalance());
        } else {
            System.out.println("[X] " + result);
        }
    }

    private void reverseTransaction() {
        System.out.println("\n--- [4] REVERSE TRANSACTION (AUDIT LEDGER CORRECTION) ---");
        String txId = readStringInput("Enter Transaction ID to reverse: ").trim();
        if (txId.isEmpty()) {
            return;
        }

        System.out.println("Executing reversal protocol...");
        String result = bankService.reverseTransaction(txId);
        if (result.startsWith("SUCCESS")) {
            System.out.println("[✓] " + result);
        } else {
            System.out.println("[X] " + result);
        }
    }

    private void searchTransaction() {
        System.out.println("\n--- [5] FAST O(1) TRANSACTION LOOKUP ---");
        String txId = readStringInput("Enter Transaction ID: ").trim();
        if (txId.isEmpty()) {
            return;
        }

        long startTime = System.nanoTime();
        Transaction tx = bankService.getTransaction(txId);
        long endTime = System.nanoTime();

        if (tx != null) {
            System.out.println("\n[✓] Transaction found:");
            System.out.println("----------------------------------------------------");
            System.out.println("ID:          " + tx.getId());
            System.out.println("Account:     " + tx.getAccount());
            System.out.println("Type:        " + tx.getType());
            System.out.printf("Amount:      %.2f\n", tx.getAmount());
            System.out.println("Timestamp:   " + tx.getTime());
            System.out.println("----------------------------------------------------");
            System.out.printf("Lookup lookup elapsed time: %d nanoseconds (~O(1) complexity)\n", (endTime - startTime));
        } else {
            System.out.println("[!] Transaction ID not found in the Hash Table ledger.");
        }
    }

    private void viewAnalyticsDashboard() {
        System.out.println("\n--- [6] 12-MONTH CASH FLOW ANALYTICS DASHBOARD ---");
        BankService.MonthlyReport[] reports = bankService.generateMonthlyAnalytics();
        
        System.out.printf("%-10s | %-15s | %-15s | %-15s | %-15s\n", 
                "Month", "Total Deposits", "Total Withdraws", "Total Reversals", "Net Cash Flow");
        System.out.println("----------------------------------------------------------------------------------");
        
        String[] months = {
            "January", "February", "March", "April", "May", "June", 
            "July", "August", "September", "October", "November", "December"
        };

        double maxVolume = -1;
        int peakMonthIdx = -1;
        
        for (int i = 0; i < 12; i++) {
            BankService.MonthlyReport rep = reports[i];
            double volume = rep.getTotalVolume();
            if (volume > maxVolume) {
                maxVolume = volume;
                peakMonthIdx = i;
            }
            System.out.printf("%-10s | %-15.2f | %-15.2f | %-15.2f | %-15.2f\n",
                    months[i], rep.depositSum, rep.withdrawalSum, rep.reversalSum, rep.getNetFlow());
        }

        System.out.println("----------------------------------------------------------------------------------");
        if (peakMonthIdx != -1 && maxVolume > 0) {
            System.out.printf("Financial Insight: Peak transaction month is %s with total volume of %.2f VND.\n",
                    months[peakMonthIdx], maxVolume);
        } else {
            System.out.println("Financial Insight: No transaction activity has been recorded yet.");
        }
    }

    private void generateSimulationData() {
        System.out.println("\n--- [7] GENERATE LARGE SIMULATION DATASET ---");
        System.out.println("This action will overwrite current local accounts and transactions files.");
        int count = readIntInput("Enter number of transactions to generate (e.g. 10000): ");
        if (count < 1000) {
            System.out.println("[!] For valid empirical benchmarks, please generate at least 1,000 transactions.");
            return;
        }

        System.out.println("Generating datasets... Please wait.");
        boolean success = DataGenerator.generateData(count, accountsPath, transactionsPath);
        if (success) {
            System.out.println("[✓] Successfully generated new datasets.");
            System.out.println("Loading updated datasets into current active memory...");
            bankService.loadFromCSV(accountsPath, transactionsPath);
            System.out.println("Active accounts loaded: " + bankService.getAccounts().length);
            System.out.println("Ledger transactions count: " + bankService.getAllTransactions().length);
        } else {
            System.out.println("[X] Generation failed.");
        }
    }

    private void runPerformanceBenchmark() {
        System.out.println("\n--- [8] RUN EMPIRICAL PERFORMANCE BENCHMARK ---");
        System.out.println("Research Question (RQ): How many times faster is custom Hash Table lookup compared to Singly Linked List?");
        System.out.println("This test runs random lookups on the active transactions database.");
        
        int count = bankService.getAllTransactions().length;
        if (count == 0) {
            System.out.println("[!] Active transactions ledger is empty. Please run Option 7 to generate data first.");
            return;
        }

        int lookups = readIntInput("Enter number of search repetitions to perform: ");
        if (lookups <= 0) {
            System.out.println("[!] Number of lookups must be positive.");
            return;
        }

        BenchmarkService.BenchmarkResult res = BenchmarkService.runBenchmark(transactionsPath, lookups);
        if (res == null) {
            System.out.println("[X] Benchmark failed to complete.");
            return;
        }

        System.out.println("\n====================================================");
        System.out.println("               BENCHMARK RESULTS REPORT             ");
        System.out.println("====================================================");
        System.out.printf("Dataset Size (N):     %,d transactions\n", res.datasetSize);
        System.out.printf("Number of Lookups:    %,d lookups\n", res.lookupCount);
        System.out.println("----------------------------------------------------");
        System.out.println("Insertion Phase:");
        System.out.printf("  - Custom Hash Table:    %,12d ns\n", res.hashInsertTimeNs);
        System.out.printf("  - Custom Linked List:   %,12d ns\n", res.listInsertTimeNs);
        System.out.println("----------------------------------------------------");
        System.out.println("Search Lookup Phase (Total search time):");
        System.out.printf("  - Custom Hash Table:    %,12d ns (avg %,.1f ns/lookup)\n", 
                res.hashSearchTimeNs, (double) res.hashSearchTimeNs / res.lookupCount);
        System.out.printf("  - Custom Linked List:   %,12d ns (avg %,.1f ns/lookup)\n", 
                res.listSearchTimeNs, (double) res.listSearchTimeNs / res.lookupCount);
        System.out.println("----------------------------------------------------");
        System.out.printf("Speedup Ratio:        Custom Hash Table is %.2fx FASTER\n", res.searchSpeedupRatio);
        System.out.println("Complexity Proof:     Hash Table lookup matches O(1) average time,");
        System.out.println("                      while Linked List matches O(n) average search time.");
        System.out.println("====================================================");
    }

    private void saveAndExit() {
        System.out.println("\nSaving transaction history and active account balances to files...");
        boolean success = bankService.saveToCSV(accountsPath, transactionsPath);
        if (success) {
            System.out.println("[✓] Data saved successfully to: ");
            System.out.println("  - " + accountsPath);
            System.out.println("  - " + transactionsPath);
        } else {
            System.out.println("[X] Warning: Fails to persist database to storage files.");
        }
        System.out.println("\nExiting Bank Transaction History System. Thank you!");
    }

    // --- Input Readers with Validation ---

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid integer format. Please try again.");
            }
        }
    }

    private double readDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid double format. Please try again.");
            }
        }
    }

    private String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
