package com.bank.service;

import com.bank.datastructure.CustomHashTable;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankService {
    private CustomHashTable transactionLedger;
    private Account[] accounts;
    private int accountCount;
    private static final int MAX_ACCOUNTS = 50000;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BankService() {
        this.transactionLedger = new CustomHashTable();
        this.accounts = new Account[MAX_ACCOUNTS];
        this.accountCount = 0;
    }

    // --- Account Management ---

    public boolean addAccount(String accNum, String accountName, double initialBalance) {
        if (findAccount(accNum) != null) {
            return false; // Account already exists
        }
        if (accountCount >= MAX_ACCOUNTS) {
            return false; // Reach capacity
        }
        accounts[accountCount++] = new Account(accNum, accountName, initialBalance);
        return true;
    }

    public boolean addAccount(String accNum, double initialBalance) {
        return addAccount(accNum, null, initialBalance);
    }

    public Account findAccount(String accNum) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equalsIgnoreCase(accNum)) {
                return accounts[i];
            }
        }
        return null;
    }

    public Account[] getAccounts() {
        Account[] activeAccounts = new Account[accountCount];
        System.arraycopy(accounts, 0, activeAccounts, 0, accountCount);
        return activeAccounts;
    }

    // --- [F1] [F2] Add Transaction with Strict Balance Control ---

    public synchronized String postTransaction(String accountNum, double amount, TransactionType type) {
        Account acc = findAccount(accountNum);
        if (acc == null) {
            return "ERROR: Account does not exist.";
        }

        if (amount <= 0) {
            return "ERROR: Amount must be positive.";
        }

        // Apply strict balance check for withdrawal (or reversal that acts as debit)
        if (type == TransactionType.WITHDRAWAL) {
            if (acc.getBalance() < amount) {
                return "ERROR: Insufficient funds. Overdraft is strictly prohibited.";
            }
            acc.withdraw(amount);
        } else if (type == TransactionType.DEPOSIT) {
            acc.deposit(amount);
        } // For REVERSAL posted directly, logic is handled in the reversal function below.

        // Generate Unique ID: TXN + Timestamp + nanoTime (last 4 digits)
        String timeStr = LocalDateTime.now().format(DATE_FORMATTER);
        long nano = System.nanoTime();
        String id = String.format("TXN%s%04d", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), nano % 10000);

        Transaction tx = new Transaction(id, accountNum, amount, type, timeStr);
        transactionLedger.put(id, tx);
        return "SUCCESS: Transaction posted with ID: " + id;
    }

    // --- [F3] No-Delete Audit Trail ---
    // Note: The service strictly does not provide delete or update interfaces on transactionLedger.

    // --- [F4] Reversal Transaction ---

    public synchronized String reverseTransaction(String originalTxId) {
        Transaction origTx = transactionLedger.get(originalTxId);
        if (origTx == null) {
            return "ERROR: Original transaction not found.";
        }

        if (origTx.getType() == TransactionType.REVERSAL) {
            return "ERROR: Cannot reverse a reversal transaction.";
        }

        Account acc = findAccount(origTx.getAccount());
        if (acc == null) {
            return "ERROR: Account for transaction no longer exists.";
        }

        double amount = origTx.getAmount();
        TransactionType revType = TransactionType.REVERSAL;

        // Balance check for reversal:
        // If original was DEPOSIT, we must subtract the money back. So check if balance has enough funds.
        if (origTx.getType() == TransactionType.DEPOSIT) {
            if (acc.getBalance() < amount) {
                return "ERROR: Insufficient funds to reverse this deposit (account has already spent the balance).";
            }
            acc.withdraw(amount);
        } else if (origTx.getType() == TransactionType.WITHDRAWAL) {
            // If original was WITHDRAWAL, we refund the money. No balance check needed.
            acc.deposit(amount);
        }

        String timeStr = LocalDateTime.now().format(DATE_FORMATTER);
        long nano = System.nanoTime();
        String revId = String.format("REV%s%04d", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), nano % 10000);

        // Record reversal transaction
        Transaction revTx = new Transaction(revId, acc.getAccountNumber(), amount, revType, timeStr);
        transactionLedger.put(revId, revTx);

        return String.format("SUCCESS: Reversed Transaction %s. Reversal recorded as ID: %s", originalTxId, revId);
    }

    // --- [F5] O(1) Fast Lookup ---

    public Transaction getTransaction(String txId) {
        return transactionLedger.get(txId);
    }

    public Transaction[] getTransactionsByAccount(String accountNum) {
        Transaction[] allTx = transactionLedger.getAll();
        int count = 0;
        for (Transaction tx : allTx) {
            if (tx != null && tx.getAccount().equalsIgnoreCase(accountNum)) {
                count++;
            }
        }
        
        Transaction[] result = new Transaction[count];
        int idx = 0;
        for (Transaction tx : allTx) {
            if (tx != null && tx.getAccount().equalsIgnoreCase(accountNum)) {
                result[idx++] = tx;
            }
        }
        return result;
    }

    public Transaction[] getTransactionsByType(TransactionType type) {
        Transaction[] allTx = transactionLedger.getAll();
        int count = 0;
        for (Transaction tx : allTx) {
            if (tx != null && tx.getType() == type) {
                count++;
            }
        }
        Transaction[] result = new Transaction[count];
        int idx = 0;
        for (Transaction tx : allTx) {
            if (tx != null && tx.getType() == type) {
                result[idx++] = tx;
            }
        }
        return result;
    }

    public Transaction[] getTransactionsByMonth(int month) {
        Transaction[] allTx = transactionLedger.getAll();
        int count = 0;
        for (Transaction tx : allTx) {
            if (tx != null && tx.getTime() != null && tx.getTime().length() >= 7) {
                try {
                    int txMonth = Integer.parseInt(tx.getTime().substring(5, 7));
                    if (txMonth == month) {
                        count++;
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        Transaction[] result = new Transaction[count];
        int idx = 0;
        for (Transaction tx : allTx) {
            if (tx != null && tx.getTime() != null && tx.getTime().length() >= 7) {
                try {
                    int txMonth = Integer.parseInt(tx.getTime().substring(5, 7));
                    if (txMonth == month) {
                        result[idx++] = tx;
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return result;
    }

    public Transaction[] getTransactionsByCombination(String accountNum, TransactionType type, int month) {
        Transaction[] allTx = transactionLedger.getAll();
        int count = 0;
        for (Transaction tx : allTx) {
            if (tx == null) continue;
            
            // Check account
            if (accountNum != null && !accountNum.isEmpty() && !tx.getAccount().equalsIgnoreCase(accountNum)) {
                continue;
            }
            
            // Check type
            if (type != null && tx.getType() != type) {
                continue;
            }
            
            // Check month
            if (month > 0 && month <= 12) {
                if (tx.getTime() == null || tx.getTime().length() < 7) {
                    continue;
                }
                try {
                    int txMonth = Integer.parseInt(tx.getTime().substring(5, 7));
                    if (txMonth != month) {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            
            count++;
        }
        
        Transaction[] result = new Transaction[count];
        int idx = 0;
        for (Transaction tx : allTx) {
            if (tx == null) continue;
            
            if (accountNum != null && !accountNum.isEmpty() && !tx.getAccount().equalsIgnoreCase(accountNum)) {
                continue;
            }
            
            if (type != null && tx.getType() != type) {
                continue;
            }
            
            if (month > 0 && month <= 12) {
                if (tx.getTime() == null || tx.getTime().length() < 7) {
                    continue;
                }
                try {
                    int txMonth = Integer.parseInt(tx.getTime().substring(5, 7));
                    if (txMonth != month) {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            
            result[idx++] = tx;
        }
        return result;
    }

    public Transaction[] getAllTransactions() {
        return transactionLedger.getAll();
    }

    // --- [F6] 12-Month Analytical Dashboard (Fixed Array of 12 Elements) ---

    public static class MonthlyReport {
        public double depositSum = 0;
        public double withdrawalSum = 0;
        public double reversalSum = 0;

        public double getNetFlow() {
            return depositSum - withdrawalSum - reversalSum;
        }

        public double getTotalVolume() {
            return depositSum + withdrawalSum + reversalSum;
        }
    }

    public MonthlyReport[] generateMonthlyAnalytics() {
        // Fixed static array representing 12 months
        MonthlyReport[] reports = new MonthlyReport[12];
        for (int i = 0; i < 12; i++) {
            reports[i] = new MonthlyReport();
        }

        Transaction[] allTx = transactionLedger.getAll();
        for (Transaction tx : allTx) {
            if (tx == null) continue;
            
            // Extract month from date string: YYYY-MM-DD HH:MM:SS (Month is at index 5 and 6)
            String dateStr = tx.getTime();
            if (dateStr == null || dateStr.length() < 7) continue;
            
            try {
                int month = Integer.parseInt(dateStr.substring(5, 7));
                int monthIdx = month - 1; // 0-indexed for 12 months array
                
                if (monthIdx >= 0 && monthIdx < 12) {
                    if (tx.getType() == TransactionType.DEPOSIT) {
                        reports[monthIdx].depositSum += tx.getAmount();
                    } else if (tx.getType() == TransactionType.WITHDRAWAL) {
                        reports[monthIdx].withdrawalSum += tx.getAmount();
                    } else if (tx.getType() == TransactionType.REVERSAL) {
                        reports[monthIdx].reversalSum += tx.getAmount();
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore parsing errors for malformed times
            }
        }
        return reports;
    }

    // --- [F7] File Persistence (BufferedReader/BufferedWriter) ---

    public boolean saveToCSV(String accountsPath, String transactionsPath) {
        // Save Accounts
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsPath))) {
            for (int i = 0; i < accountCount; i++) {
                writer.write(accounts[i].toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
            return false;
        }

        // Save Transactions
        Transaction[] allTx = transactionLedger.getAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsPath))) {
            for (Transaction tx : allTx) {
                if (tx != null) {
                    writer.write(tx.toCSV());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean loadFromCSV(String accountsPath, String transactionsPath) {
        // Clear current states
        this.accountCount = 0;
        this.transactionLedger = new CustomHashTable();

        // Load Accounts
        File accFile = new File(accountsPath);
        if (accFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(accFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    Account acc = Account.fromCSV(line);
                    if (acc != null && accountCount < MAX_ACCOUNTS) {
                        accounts[accountCount++] = acc;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading accounts: " + e.getMessage());
                return false;
            }
        }

        // Load Transactions
        File txFile = new File(transactionsPath);
        if (txFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(txFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    Transaction tx = Transaction.fromCSV(line);
                    if (tx != null) {
                        transactionLedger.put(tx.getId(), tx);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading transactions: " + e.getMessage());
                return false;
            }
        }

        return true;
    }
}
