/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BankTransaction;

public class ReportManager extends Report {

    private double[] monthlyTotal =
            new double[12];

    public void addAmount(
            int month,
            double amount) {

        monthlyTotal[month - 1] += amount;
    }

    @Override
    public void displayReport() {

        System.out.println(
                "\n===== MONTHLY REPORT =====");

        for (int i = 0; i < 12; i++) {

            System.out.println(
                    "Month "
                    + (i + 1)
                    + ": "
                    + monthlyTotal[i]);
        }
    }
}