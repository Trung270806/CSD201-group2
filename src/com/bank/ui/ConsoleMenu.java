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
            try {
                int choice = readIntInput("Select an option (1-8): ");
                switch (choice) {
                    case 1:
                        viewAccounts();
                        break;
                    case 2:
                        addNewAccount();
                        break;
                    case 3:
                        viewTransactionHistory();
                        break;
                    case 4:
                        postNewTransaction();
                        break;
                    case 5:
                        searchTransaction();
                        break;
                    case 6:
                        viewAnalyticsDashboard();
                        break;
                    case 7:
                        runPerformanceBenchmark();
                        break;
                    case 8:
                        exitApp();
                        running = false;
                        break;
                    default:
                        System.out.println("\n[!] Invalid choice. Please choose a value from 1 to 8.");
                }
            } catch (OperationCancelledException e) {
                System.out.println("\n[i] " + e.getMessage());
            }
        }
    }

    private void printMenuHeader() {
        System.out.println("\n====================================================");
        System.out.println("             BankTransaction Interface              ");
        System.out.println("====================================================");
        System.out.println("1. View Accounts & Balances");
        System.out.println("2. Add New Account");
        System.out.println("3. View Transaction History");
        System.out.println("4. Post New Transaction (Deposit/Withdrawal)");
        System.out.println("5. Fast Lookup by Transaction ID [O(1) Hash Table]");
        System.out.println("6. View 12-Month Cash Flow Analytics Dashboard");
        System.out.println("7. Run Performance Benchmark (Hash Table vs. Linked List)");
        System.out.println("8. Exit");
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
        String accNum = readStringInput("Enter new account number (e.g. ACC000001) [type 'exit' to cancel]: ").trim();
        if (accNum.isEmpty()) {
            System.out.println("[!] Account number cannot be empty.");
            return;
        }
        double balance = readDoubleInput("Enter initial balance [type 'exit' to cancel]: ");
        if (balance < 0) {
            System.out.println("[!] Initial balance cannot be negative.");
            return;
        }

        boolean success = bankService.addAccount(accNum, balance);
        if (success) {
            System.out.printf("[+] Account %s successfully created.\n", accNum);
            bankService.saveToCSV(accountsPath, transactionsPath);
        } else {
            System.out.println("[!] Failed to create account. It might already exist or system is at capacity.");
        }
    }

    private void viewTransactionHistory() {
        System.out.println("\n--- [3] TRANSACTION HISTORY ---");
        long startTime = System.nanoTime();
        Transaction[] txs = bankService.getAllTransactions();
        long endTime = System.nanoTime();
        displayTransactionsList(txs, endTime - startTime);
    }

    private void postNewTransaction() {
        System.out.println("\n--- [4] POST NEW TRANSACTION ---");
        String accNum = readStringInput("Enter account number [type 'exit' to cancel]: ").trim();
        Account acc = bankService.findAccount(accNum);
        if (acc == null) {
            System.out.println("[!] Account not found.");
            return;
        }
        System.out.println("Current Account Balance: " + acc.getBalance());

        boolean transactionDone = false;
        while (!transactionDone) {
            System.out.println("\nSelect Type: 1. DEPOSIT | 2. WITHDRAWAL | 3. EXIT/CANCEL");
            int typeChoice = readIntInput("Your choice: ");
            TransactionType type;
            if (typeChoice == 1) {
                type = TransactionType.DEPOSIT;
            } else if (typeChoice == 2) {
                type = TransactionType.WITHDRAWAL;
            } else if (typeChoice == 3) {
                System.out.println("Operation cancelled.");
                return;
            } else {
                System.out.println("[!] Invalid type selection.");
                continue;
            }

            double amount = readDoubleInput("Enter transaction amount [type 'exit' to cancel]: ");
            if (amount <= 0) {
                System.out.println("[!] Transaction amount must be positive.");
                continue;
            }

            // Confirm step
            System.out.println("\n[?] Confirm transaction details:");
            System.out.println("    Account: " + accNum);
            System.out.println("    Type:    " + type);
            System.out.printf("    Amount:  %,.2f VND\n", amount);
            System.out.println("------------------------------------");
            System.out.println("1. Confirm transaction");
            System.out.println("2. Back to type selection");
            System.out.println("3. Exit/Cancel transaction");
            int confirmChoice = readIntInput("Your choice (1-3): ");

            if (confirmChoice == 1) {
                String result = bankService.postTransaction(accNum, amount, type);
                if (result.startsWith("SUCCESS")) {
                    System.out.println("[+] " + result);
                    System.out.println("New Account Balance: " + acc.getBalance());
                    bankService.saveToCSV(accountsPath, transactionsPath);
                } else {
                    System.out.println("[-] " + result);
                }
                transactionDone = true;
            } else if (confirmChoice == 2) {
                System.out.println("Returning to transaction type selection...");
            } else if (confirmChoice == 3) {
                System.out.println("Operation cancelled.");
                return;
            } else {
                System.out.println("[!] Invalid choice. Returning to transaction type selection...");
            }
        }
    }

    private void searchTransaction() {
        System.out.println("\n--- [5] FAST TRANSACTION LOOKUP ---");
        System.out.println("Select Search Mode:");
        System.out.println("1. Search By ID (Transaction ID / Account Number)");
        System.out.println("2. Search By Type (DEPOSIT / WITHDRAWAL)");
        System.out.println("3. Search By Month (1-12)");
        System.out.println("4. Search By Combination");
        System.out.println("5. Exit/Back to Main Menu");
        int mode = readIntInput("Your choice (1-5): ");

        if (mode == 5) {
            return;
        }

        if (mode == 1) {
            System.out.println("\nSelect ID Type:");
            System.out.println("1. Search by Transaction ID [O(1) Hash Table]");
            System.out.println("2. Search by Account Number (User ID)");
            System.out.println("3. Exit/Back to Search Menu");
            int idChoice = readIntInput("Your choice (1-3): ");

            if (idChoice == 3) {
                return;
            }

            if (idChoice == 1) {
                String txId = readStringInput("Enter Transaction ID [type 'exit' to cancel]: ").trim();
                if (txId.isEmpty()) return;

                long startTime = System.nanoTime();
                Transaction tx = bankService.getTransaction(txId);
                long endTime = System.nanoTime();

                if (tx != null) {
                    System.out.println("\n[+] Transaction found:");
                    System.out.println("----------------------------------------------------");
                    System.out.println("ID:          " + tx.getId());
                    System.out.println("Account:     " + tx.getAccount());
                    System.out.println("Type:        " + tx.getType());
                    System.out.printf("Amount:      %,.2f VND\n", tx.getAmount());
                    System.out.println("Timestamp:   " + tx.getTime());
                    System.out.println("----------------------------------------------------");
                    System.out.printf("Lookup elapsed time: %d nanoseconds (~O(1) complexity)\n", (endTime - startTime));
                } else {
                    System.out.println("[-] Transaction ID not found in the Hash Table ledger.");
                }
            } else if (idChoice == 2) {
                String accNum = readStringInput("Enter Account Number (User ID) [type 'exit' to cancel]: ").trim();
                if (accNum.isEmpty()) return;

                Account acc = bankService.findAccount(accNum);
                if (acc == null) {
                    System.out.println("[-] Account not found.");
                    return;
                }

                long startTime = System.nanoTime();
                Transaction[] txs = bankService.getTransactionsByAccount(accNum);
                long endTime = System.nanoTime();

                System.out.println("\n[+] Transactions for Account: " + accNum);
                System.out.printf("Current Balance: %,.2f VND\n", acc.getBalance());
                displayTransactionsList(txs, endTime - startTime);
            } else {
                System.out.println("[-] Invalid choice.");
            }

        } else if (mode == 2) {
            System.out.println("\nSelect Transaction Type:");
            System.out.println("1. DEPOSIT");
            System.out.println("2. WITHDRAWAL");
            System.out.println("3. Exit/Back to Search Menu");
            int typeChoice = readIntInput("Your choice (1-3): ");
            TransactionType type = null;
            if (typeChoice == 1) type = TransactionType.DEPOSIT;
            else if (typeChoice == 2) type = TransactionType.WITHDRAWAL;
            else if (typeChoice == 3) return;
            else {
                System.out.println("[-] Invalid type choice.");
                return;
            }

            long startTime = System.nanoTime();
            Transaction[] txs = bankService.getTransactionsByType(type);
            long endTime = System.nanoTime();

            System.out.println("\n[+] Transactions of Type: " + type);
            displayTransactionsList(txs, endTime - startTime);

        } else if (mode == 3) {
            int month = readIntInput("Enter Month (1-12) [type 'exit' or enter 0 to cancel]: ");
            if (month == 0) {
                System.out.println("Operation cancelled.");
                return;
            }
            if (month < 1 || month > 12) {
                System.out.println("[-] Invalid month. Must be between 1 and 12.");
                return;
            }

            long startTime = System.nanoTime();
            Transaction[] txs = bankService.getTransactionsByMonth(month);
            long endTime = System.nanoTime();

            System.out.println("\n[+] Transactions recorded in Month: " + month);
            displayTransactionsList(txs, endTime - startTime);

        } else if (mode == 4) {
            System.out.println("\n--- SEARCH BY COMBINATION ---");
            String accNum = readStringInput("Enter Account Number (User ID) [type 'exit' to cancel or press Enter to skip]: ").trim();
            
            System.out.println("Select Transaction Type:");
            System.out.println("0. Skip filtering by Type");
            System.out.println("1. DEPOSIT");
            System.out.println("2. WITHDRAWAL");
            System.out.println("3. Exit/Cancel");
            int typeChoice = readIntInput("Your choice (0-3): ");
            if (typeChoice == 3) {
                System.out.println("Operation cancelled.");
                return;
            }
            TransactionType type = null;
            if (typeChoice == 1) type = TransactionType.DEPOSIT;
            else if (typeChoice == 2) type = TransactionType.WITHDRAWAL;

            int month = readIntInput("Enter Month (1-12) [0 to skip, -1 to exit/cancel]: ");
            if (month == -1) {
                System.out.println("Operation cancelled.");
                return;
            }
            if (month < 0 || month > 12) {
                System.out.println("[-] Invalid month.");
                return;
            }

            long startTime = System.nanoTime();
            Transaction[] txs = bankService.getTransactionsByCombination(accNum, type, month);
            long endTime = System.nanoTime();

            System.out.println("\n[+] Search Combination Results:");
            if (accNum.length() > 0) System.out.println("    Account: " + accNum);
            if (type != null) System.out.println("    Type:    " + type);
            if (month > 0) System.out.println("    Month:   " + month);
            
            displayTransactionsList(txs, endTime - startTime);

        } else {
            System.out.println("[-] Invalid lookup mode selected.");
        }
    }

    private void displayTransactionsList(Transaction[] txs, long elapsedNs) {
        if (txs == null || txs.length == 0) {
            System.out.println("No transactions matching criteria found.");
        } else {
            System.out.println("------------------------------------------------------------------------------------------------------");
            System.out.printf("%-25s | %-12s | %-15s | %-18s | %-20s\n", "Transaction ID", "Account ID", "Type", "Amount (VND)", "Timestamp");
            System.out.println("------------------------------------------------------------------------------------------------------");
            for (Transaction tx : txs) {
                if (tx != null) {
                    System.out.printf("%-25s | %-12s | %-15s | %-18.2f | %-20s\n",
                            tx.getId(), tx.getAccount(), tx.getType(), tx.getAmount(), tx.getTime());
                }
            }
            System.out.println("------------------------------------------------------------------------------------------------------");
            System.out.printf("Retrieved %d transactions. Query elapsed time: %d nanoseconds\n", txs.length, elapsedNs);
        }
    }

    private void viewAnalyticsDashboard() {
        System.out.println("\n--- [6] 12-MONTH CASH FLOW ANALYTICS DASHBOARD ---");
        BankService.MonthlyReport[] reports = bankService.generateMonthlyAnalytics();
        
        System.out.printf("%-10s | %-15s | %-15s\n", 
                "Month", "Total Deposits", "Total Withdraws");
        System.out.println("--------------------------------------------------");
        
        String[] months = {
            "January", "February", "March", "April", "May", "June", 
            "July", "August", "September", "October", "November", "December"
        };

        double maxVolume = -1;
        int peakMonthIdx = -1;
        
        for (int i = 0; i < 12; i++) {
            BankService.MonthlyReport rep = reports[i];
            double volume = rep.depositSum + rep.withdrawalSum;
            if (volume > maxVolume) {
                maxVolume = volume;
                peakMonthIdx = i;
            }
            System.out.printf("%-10s | %-15.2f | %-15.2f\n",
                    months[i], rep.depositSum, rep.withdrawalSum);
        }

        System.out.println("--------------------------------------------------");
        if (peakMonthIdx != -1 && maxVolume > 0) {
            System.out.printf("Financial Insight: Peak transaction month is %s with total volume of %.2f VND.\n",
                    months[peakMonthIdx], maxVolume);
        } else {
            System.out.println("Financial Insight: No transaction activity has been recorded yet.");
        }
    }

    private void runPerformanceBenchmark() {
        System.out.println("\n--- [7] RUN EMPIRICAL PERFORMANCE BENCHMARK ---");
        System.out.println("Research Question (RQ): Addressing RQ1, RQ2, and RQ3 for FPT CSD201 Assignment.");
        
        int count = bankService.getAllTransactions().length;
        if (count == 0) {
            System.out.println("[!] Active transactions ledger is empty. Please restart the application to generate data.");
            return;
        }

        int lookups = readIntInput("Enter number of repetitions to perform for queries (e.g. 1000) [type 'exit' or 0 to cancel]: ");
        if (lookups <= 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        BenchmarkService.BenchmarkResult res = BenchmarkService.runBenchmark(transactionsPath, lookups);
        if (res == null) {
            System.out.println("[-] Benchmark failed to complete.");
            return;
        }

        BenchmarkService.printReportToConsole(res, lookups);
    }

    private void exitApp() {
        System.out.println("\nExiting Bank Transaction History System. Thank you!");
    }

    // --- Input Readers with Validation ---

    private static class OperationCancelledException extends RuntimeException {
        public OperationCancelledException() {
            super("Operation cancelled by user.");
        }
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                throw new OperationCancelledException();
            }
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
            if (input.equalsIgnoreCase("exit")) {
                throw new OperationCancelledException();
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid double format. Please try again.");
            }
        }
    }

    private String readStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit")) {
            throw new OperationCancelledException();
        }
        return input;
    }
}
