package com.bank.datastructure;

import com.bank.model.Transaction;

public class CustomHashTable {
    private static class Node {
        String key;
        Transaction value;
        Node next;

        public Node(String key, Transaction value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Node[] table;
    private int size;
    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    public CustomHashTable() {
        this(INITIAL_CAPACITY);
    }

    public CustomHashTable(int capacity) {
        this.table = new Node[capacity];
        this.size = 0;
    }

    private int hash(String key) {
        int h = 0;
        for (int i = 0; i < key.length(); i++) {
            h = 31 * h + key.charAt(i);
        }
        return Math.abs(h) % table.length;
    }

    public void put(String key, Transaction value) {
        if (key == null) return;
        
        // Dynamic resizing when load factor exceeds threshold
        if ((double) size / table.length >= LOAD_FACTOR_THRESHOLD) {
            resize(table.length * 2);
        }

        int index = hash(key);
        Node current = table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // Update if key exists
                return;
            }
            current = current.next;
        }

        // Insert at the head of the bucket list (chaining)
        table[index] = new Node(key, value, table[index]);
        size++;
    }

    public Transaction get(String key) {
        if (key == null) return null;
        int index = hash(key);
        Node current = table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null; // Not found
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void resize(int newCapacity) {
        Node[] oldTable = table;
        table = new Node[newCapacity];
        size = 0; // reset size, it will be incremented in put()

        for (Node head : oldTable) {
            Node current = head;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }

    /**
     * Retrieves all Transactions stored in the hash table as a standard array.
     */
    public Transaction[] getAll() {
        Transaction[] allTransactions = new Transaction[size];
        int count = 0;
        for (Node head : table) {
            Node current = head;
            while (current != null) {
                allTransactions[count++] = current.value;
                current = current.next;
            }
        }
        return allTransactions;
    }
}
