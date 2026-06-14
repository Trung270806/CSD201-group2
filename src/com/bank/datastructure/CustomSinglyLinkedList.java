package com.bank.datastructure;

import com.bank.model.Transaction;

public class CustomSinglyLinkedList {
    private static class Node {
        Transaction value;
        Node next;

        public Node(Transaction value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public CustomSinglyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(Transaction tx) {
        if (tx == null) return;
        Node newNode = new Node(tx);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public Transaction find(String txId) {
        if (txId == null) return null;
        Node current = head;
        while (current != null) {
            if (current.value.getId().equals(txId)) {
                return current.value;
            }
            current = current.next;
        }
        return null; // Not found
    }

    public int size() {
        return size;
    }

    public Transaction[] getAll() {
        Transaction[] allTransactions = new Transaction[size];
        Node current = head;
        int index = 0;
        while (current != null) {
            allTransactions[index++] = current.value;
            current = current.next;
        }
        return allTransactions;
    }
}
