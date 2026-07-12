package com.bank.model;

public class Transaction {
    private String id;
    private String account;
    private double amount;
    private TransactionType type;
    private String time; // format YYYY-MM-DD HH:MM:SS

    public Transaction(String id, String account, double amount, TransactionType type, String time) {
        this.id = id;
        this.account = account;
        this.amount = amount;
        this.type = type;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String toCSV() {
        return String.format(java.util.Locale.US, "%s,%s,%.2f,%s,%s", id, account, amount, type.name(), time);
    }

    public static Transaction fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 5) {
            return null;
        }
        String id = parts[0];
        String account = parts[1];
        double amount = Double.parseDouble(parts[2]);
        TransactionType type = TransactionType.valueOf(parts[3]);
        String time = parts[4];
        return new Transaction(id, account, amount, type, time);
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "Transaction[ID: %s, Account: %s, Amount: %.2f, Type: %s, Time: %s]",
                id, account, amount, type, time);
    }
}
