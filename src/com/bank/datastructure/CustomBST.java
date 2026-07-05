package com.bank.datastructure;

import com.bank.model.Transaction;
import java.util.ArrayList;
import java.util.List;

public class CustomBST {
    public static class Node {
        public Transaction value;
        public Node left;
        public Node right;

        public Node(Transaction value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    private int size;

    public CustomBST() {
        this.root = null;
        this.size = 0;
    }

    public void insert(Transaction tx) {
        if (tx == null || tx.getTime() == null) return;
        root = insertRec(root, tx);
        size++;
    }

    private Node insertRec(Node root, Transaction tx) {
        if (root == null) {
            return new Node(tx);
        }
        // Ordered by transaction time string (chronological order)
        if (tx.getTime().compareTo(root.value.getTime()) <= 0) {
            root.left = insertRec(root.left, tx);
        } else {
            root.right = insertRec(root.right, tx);
        }
        return root;
    }

    /**
     * Range Query: Retrieves all transactions in range [startTime, endTime] (inclusive).
     * Time Complexity: O(log N + k) on average where N is tree size and k is number of matches.
     */
    public Transaction[] findRange(String startTime, String endTime) {
        List<Transaction> matches = new ArrayList<>();
        findRangeRec(root, startTime, endTime, matches);
        return matches.toArray(new Transaction[0]);
    }

    private void findRangeRec(Node node, String startTime, String endTime, List<Transaction> matches) {
        if (node == null) return;

        String time = node.value.getTime();

        // 1. If current node's time is greater than startTime, search left subtree
        if (time.compareTo(startTime) > 0) {
            findRangeRec(node.left, startTime, endTime, matches);
        }

        // 2. If current node is within range, add it
        if (time.compareTo(startTime) >= 0 && time.compareTo(endTime) <= 0) {
            matches.add(node.value);
        }

        // 3. If current node's time is less than endTime, search right subtree
        if (time.compareTo(endTime) < 0) {
            findRangeRec(node.right, startTime, endTime, matches);
        }
    }

    public int size() {
        return size;
    }

    public Node getRoot() {
        return root;
    }
}
