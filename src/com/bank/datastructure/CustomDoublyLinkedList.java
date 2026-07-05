package com.bank.datastructure;

import com.bank.model.Transaction;

public class CustomDoublyLinkedList {
    public static class Node {
        public Transaction value;
        public Node next;
        public Node prev;

        public Node(Transaction value) {
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public CustomDoublyLinkedList() {
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
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    /**
     * Range Query: Returns all transactions between startTime and endTime (inclusive).
     * Time Complexity: O(n) because it's a linear scan.
     */
    public Transaction[] findRange(String startTime, String endTime) {
        // First count matches
        int matchCount = 0;
        Node current = head;
        while (current != null) {
            String time = current.value.getTime();
            if (time != null && time.compareTo(startTime) >= 0 && time.compareTo(endTime) <= 0) {
                matchCount++;
            }
            current = current.next;
        }

        Transaction[] result = new Transaction[matchCount];
        int idx = 0;
        current = head;
        while (current != null) {
            String time = current.value.getTime();
            if (time != null && time.compareTo(startTime) >= 0 && time.compareTo(endTime) <= 0) {
                result[idx++] = current.value;
            }
            current = current.next;
        }
        return result;
    }

    public int size() {
        return size;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }
}
