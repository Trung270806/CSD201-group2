package com.bank.model;

public class Account {
    private String accountNumber;
    private double balance;

    public Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        this.balance += amount;
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (this.balance < amount) return false; // Strict balance enforcement
        this.balance -= amount;
        return true;
    }

    public String toCSV() {
        return String.format(java.util.Locale.US, "%s,%.2f", accountNumber, balance);
    }

    public static Account fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 2) return null;
        String accountNumber = parts[0];
        double balance = Double.parseDouble(parts[1]);
        return new Account(accountNumber, balance);
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "Account[Number: %s, Balance: %.2f]", accountNumber, balance);
    }
}
