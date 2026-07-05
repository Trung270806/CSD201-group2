package com.bank.datastructure;

import com.bank.model.Transaction;

public class CustomSortedLinkedList {
    public static class Node {
        public Transaction value;
        public Node next;

        public Node(Transaction value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public CustomSortedLinkedList() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Inserts a transaction in sorted order of its Transaction ID.
     * Time Complexity: O(n) because it traverses the list to find the correct insertion spot.
     */
    public void addSorted(Transaction tx) {
        if (tx == null || tx.getId() == null) return;
        Node newNode = new Node(tx);

        // Case 1: List is empty or new node belongs at the head
        if (head == null || tx.getId().compareTo(head.value.getId()) < 0) {
            newNode.next = head;
            head = newNode;
        } else {
            // Case 2: Traverse to find insertion point
            Node current = head;
            while (current.next != null && current.next.value.getId().compareTo(tx.getId()) < 0) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    /**
     * Linear Search: Sequentially scans the list to locate the transaction ID.
     * Time Complexity: O(n)
     */
    public Transaction findLinear(String txId) {
        if (txId == null) return null;
        Node current = head;
        while (current != null) {
            if (current.value.getId().equals(txId)) {
                return current.value;
            }
            // Optimization: since the list is sorted, if current ID is larger than target, target is not present
            if (current.value.getId().compareTo(txId) > 0) {
                break;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Binary Search on a Linked List:
     * Recursively narrows the search range by finding the middle node.
     * Time Complexity: O(n) due to linear scans to find the middle node at each division step.
     */
    public Transaction findBinary(String txId) {
        if (txId == null || head == null) return null;
        return binarySearchRec(head, null, txId);
    }

    private Transaction binarySearchRec(Node startNode, Node endNode, String txId) {
        if (startNode == null || startNode == endNode) {
            return null;
        }

        // Find the middle node between startNode and endNode
        Node midNode = getMiddleNode(startNode, endNode);
        if (midNode == null) {
            return null;
        }

        int comp = txId.compareTo(midNode.value.getId());
        if (comp == 0) {
            return midNode.value;
        } else if (comp < 0) {
            // Search left half
            return binarySearchRec(startNode, midNode, txId);
        } else {
            // Search right half
            return binarySearchRec(midNode.next, endNode, txId);
        }
    }

    /**
     * Finds the middle node between startNode and endNode using the two-pointer technique.
     * Time Complexity: O(R) where R is the length of the sublist.
     */
    private Node getMiddleNode(Node startNode, Node endNode) {
        if (startNode == null) return null;
        Node slow = startNode;
        Node fast = startNode;

        while (fast != endNode && fast.next != endNode) {
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }

    public int size() {
        return size;
    }

    public Node getHead() {
        return head;
    }
}
