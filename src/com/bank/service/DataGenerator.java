package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {

    /**
     * Generates simulated account and transaction history datasets.
     *
     * @param transactionCount the number of transactions to generate (N >= 10,000).
     * @param accountsPath     destination file path for accounts CSV.
     * @param transactionsPath  destination file path for transactions CSV.
     * @return true if generation was successful.
     */
    public static boolean generateData(int transactionCount, String accountsPath, String transactionsPath) {
        Random rand = new Random();
        
        // Ensure parent directories exist
        createParentDirectories(accountsPath);
        createParentDirectories(transactionsPath);

        // 1. Generate Accounts
        int accountCount = 100; // Generate 100 unique accounts
        Account[] accounts = new Account[accountCount];
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsPath))) {
            for (int i = 0; i < accountCount; i++) {
                String accNum = String.format("ACC%06d", 100000 + i);
                double balance = 50000.0 + rand.nextDouble() * 50000.0; // Starting balance between 50k and 100k
                accounts[i] = new Account(accNum, balance);
                writer.write(accounts[i].toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing accounts file: " + e.getMessage());
            return false;
        }

        // 2. Generate Transactions
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsPath))) {
            for (int i = 0; i < transactionCount; i++) {
                String id = String.format("TXN2026%08d", i + 1);
                
                // Select random account
                int accIdx = rand.nextInt(accountCount);
                Account account = accounts[accIdx];
                
                // Select transaction type: 65% DEPOSIT, 30% WITHDRAWAL, 5% REVERSAL
                double typeRand = rand.nextDouble();
                TransactionType type;
                double amount;
                if (typeRand < 0.65) {
                    type = TransactionType.DEPOSIT;
                    amount = 500.0 + rand.nextDouble() * 4500.0; // deposit between 500 and 5000
                    account.deposit(amount);
                } else if (typeRand < 0.95) {
                    type = TransactionType.WITHDRAWAL;
                    amount = 100.0 + rand.nextDouble() * 2000.0; // withdraw between 100 and 2000
                    // Check balance to keep mock state consistent
                    if (account.getBalance() >= amount) {
                        account.withdraw(amount);
                    } else {
                        // Fallback to deposit if withdrawal would empty account
                        type = TransactionType.DEPOSIT;
                        amount = 500.0 + rand.nextDouble() * 4500.0;
                        account.deposit(amount);
                    }
                } else {
                    type = TransactionType.REVERSAL;
                    amount = 100.0 + rand.nextDouble() * 1000.0; // reversal amount
                    // Check balance constraint just in case it behaves like debit
                    if (account.getBalance() >= amount) {
                        account.withdraw(amount);
                    } else {
                        type = TransactionType.DEPOSIT;
                        amount = 500.0 + rand.nextDouble() * 4500.0;
                        account.deposit(amount);
                    }
                }

                // Random distribution over 12 months of year 2026
                int month = 1 + rand.nextInt(12);
                int day = 1 + rand.nextInt(28); // to avoid leap/day count complexities
                int hour = rand.nextInt(24);
                int minute = rand.nextInt(60);
                int second = rand.nextInt(60);
                String timeString = String.format("2026-%02d-%02d %02d:%02d:%02d", month, day, hour, minute, second);

                Transaction tx = new Transaction(id, account.getAccountNumber(), amount, type, timeString);
                writer.write(tx.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing transactions file: " + e.getMessage());
            return false;
        }

        // 3. Rewrite Accounts file with the updated balances from transaction execution
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsPath))) {
            for (Account account : accounts) {
                writer.write(account.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating accounts file: " + e.getMessage());
            return false;
        }

        return true;
    }

    private static void createParentDirectories(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir");
        String dataDir = currentDir + File.separator + "data";
        String accountsPath = dataDir + File.separator + "accounts.csv";
        String transactionsPath = dataDir + File.separator + "transactions.csv";

        System.out.println("Generating 10,000 initial transactions to data/...");
        boolean success = generateData(10000, accountsPath, transactionsPath);
        if (success) {
            System.out.println("Data generation completed successfully.");
        } else {
            System.err.println("Data generation failed.");
        }
    }
}
