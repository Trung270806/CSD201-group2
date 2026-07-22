package com.bank.model;

import java.util.Random;

public class Account {
    private String accountNumber;
    private String accountName;
    private double balance;

    private static final String[] LAST_NAMES = {
        "Nguyen", "Tran", "Le", "Pham", "Hoang", "Huynh", "Vu", "Vo", "Dang", "Bui", "Do", "Ho", "Ngo", "Duong", "Ly"
    };

    private static final String[] MIDDLE_NAMES = {
        "Van", "Thi", "Hoang", "Quoc", "Duc", "Minh", "Hai", "Ngoc", "Dinh", "Phuong", "Thanh", "Khanh", "Bao", "Anh"
    };

    private static final String[] FIRST_NAMES = {
        "Anh", "Binh", "Cuong", "Dung", "Giang", "Hai", "Hung", "Huy", "Khanh", "Linh",
        "Long", "Minh", "Nam", "Phong", "Phuc", "Quan", "Son", "Tam", "Thao", "Trang",
        "Tuan", "Viet", "Vinh", "Hoa", "Lan", "Mai", "Phuong", "Vy"
    };

    public static String generateRandomVietnameseName(String seedStr) {
        Random rand = (seedStr != null) ? new Random(seedStr.hashCode()) : new Random();
        String lastName = LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
        String middleName = MIDDLE_NAMES[rand.nextInt(MIDDLE_NAMES.length)];
        String firstName = FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)];
        return lastName + " " + middleName + " " + firstName;
    }

    public Account(String accountNumber, String accountName, double balance) {
        this.accountNumber = accountNumber;
        this.accountName = (accountName != null && !accountName.trim().isEmpty())
                ? accountName.trim()
                : generateRandomVietnameseName(accountNumber);
        this.balance = balance;
    }

    public Account(String accountNumber, double balance) {
        this(accountNumber, generateRandomVietnameseName(accountNumber), balance);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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
        return String.format(java.util.Locale.US, "%s,%s,%.2f", accountNumber, accountName, balance);
    }

    public static Account fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 2) return null;
        if (parts.length == 2) {
            String accountNumber = parts[0].trim();
            double balance = Double.parseDouble(parts[1].trim());
            return new Account(accountNumber, balance);
        } else {
            String accountNumber = parts[0].trim();
            String accountName = parts[1].trim();
            double balance = Double.parseDouble(parts[2].trim());
            return new Account(accountNumber, accountName, balance);
        }
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "Account[Number: %s, Name: %s, Balance: %.2f]", accountNumber, accountName, balance);
    }
}

