/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


package BankTransaction;

import java.util.Scanner;
import java.util.Stack;

public class BankTransaction {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        TransactionLinkedList list =
                new TransactionLinkedList();

        ReportManager report =
                new ReportManager();

        Stack<Action> undoStack =
                new Stack<Action>();

        while (true) {

            System.out.println("\n====================");
            System.out.println("BANK TRANSACTION");
            System.out.println("====================");

            System.out.println("1. Add Transaction");
            System.out.println("2. Display History");
            System.out.println("3. Search By ID");
            System.out.println("4. Delete Transaction");
            System.out.println("5. Undo");
            System.out.println("6. Monthly Report");
            System.out.println("7. Exit");

            System.out.print("Choose: ");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:

                    System.out.print("ID: ");
                    int id = sc.nextInt();

                    sc.nextLine();

                    System.out.print("Type: ");
                    String type = sc.nextLine();

                    System.out.print("Amount: ");
                    double amount = sc.nextDouble();

                    System.out.print("Month: ");
                    int month = sc.nextInt();

                    sc.nextLine();

                    System.out.print("Description: ");
                    String des = sc.nextLine();

                    Transaction t =
                            new Transaction(
                                    id,
                                    type,
                                    amount,
                                    month,
                                    des);

                    list.add(t);

                    report.addAmount(
                            month,
                            amount);

                    undoStack.push(
                            new Action(
                                    "ADD",
                                    t));

                    System.out.println(
                            "Added Successfully!");

                    break;

                case 2:

                    list.display();

                    break;

                case 3:

                    System.out.print(
                            "Search ID: ");

                    int searchId =
                            sc.nextInt();

                    Transaction found =
                            list.searchById(
                                    searchId);

                    if (found != null) {

                        found.display();

                    } else {

                        System.out.println(
                                "Not Found");
                    }

                    break;

                case 4:

                    System.out.print(
                            "Delete ID: ");

                    int deleteId =
                            sc.nextInt();

                    Transaction deleted =
                            list.delete(
                                    deleteId);

                    if (deleted != null) {

                        undoStack.push(
                                new Action(
                                        "DELETE",
                                        deleted));

                        System.out.println(
                                "Deleted!");
                    } else {

                        System.out.println(
                                "Not Found");
                    }

                    break;

                case 5:

                    if (undoStack.isEmpty()) {

                        System.out.println(
                                "Nothing to Undo");

                        break;
                    }

                    Action action =
                            undoStack.pop();

                    if (action.actionType
                            .equals("ADD")) {

                        list.delete(
                                action.transaction
                                        .getId());

                        System.out.println(
                                "Undo Add Success");

                    } else {

                        list.add(
                                action.transaction);

                        System.out.println(
                                "Undo Delete Success");
                    }

                    break;

                case 6:

                    report.displayReport();

                    break;

                case 7:

                    System.exit(0);

                default:

                    System.out.println(
                            "Invalid Choice");
            }
        }
    }
}