/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BankTransaction;

public class Transaction extends Record {

    private String type;
    private double amount;
    private int month;
    private String description;

    public Transaction(int id,
            String type,
            double amount,
            int month,
            String description) {

        super(id);

        this.type = type;
        this.amount = amount;
        this.month = month;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public int getMonth() {
        return month;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void display() {

        System.out.println(
                "ID: " + id
                + " | Type: " + type
                + " | Amount: " + amount
                + " | Month: " + month
                + " | Description: " + description);
    }
}