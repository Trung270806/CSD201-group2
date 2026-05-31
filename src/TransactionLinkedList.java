/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BankTransaction;

public class TransactionLinkedList {

    private TransactionNode head;

    public void add(Transaction t) {

        TransactionNode newNode =
                new TransactionNode(t);

        if (head == null) {

            head = newNode;
            return;
        }

        TransactionNode temp = head;

        while (temp.next != null) {

            temp = temp.next;
        }

        temp.next = newNode;
    }

    public void display() {

        if (head == null) {

            System.out.println("No transaction found.");
            return;
        }

        TransactionNode temp = head;

        while (temp != null) {

            temp.data.display();
            temp = temp.next;
        }
    }

    public Transaction searchById(int id) {

        TransactionNode temp = head;

        while (temp != null) {

            if (temp.data.getId() == id) {

                return temp.data;
            }

            temp = temp.next;
        }

        return null;
    }

    public Transaction delete(int id) {

        if (head == null) {

            return null;
        }

        if (head.data.getId() == id) {

            Transaction deleted =
                    head.data;

            head = head.next;

            return deleted;
        }

        TransactionNode prev = head;
        TransactionNode curr = head.next;

        while (curr != null) {

            if (curr.data.getId() == id) {

                Transaction deleted =
                        curr.data;

                prev.next = curr.next;

                return deleted;
            }

            prev = curr;
            curr = curr.next;
        }

        return null;
    }
}