# 💳 Bank Transaction History System

## 📌 Project Information

**Course:** CSD201 - Data Structures and Algorithms

**Topic 7:** Bank Transaction History System

**Project Goal:**
Develop a banking transaction management system that stores transaction history in chronological order, supports fast searching, deletion, undo operations, and generates monthly transaction reports.

---

## 👨‍💻 Group 2 Members

| Student ID | Full Name           | Role                    |
| ---------- | ------------------- | ----------------------- |
| QE190050   | Mai Hoàng Đăng      | Team Leader / Developer |
| QE190253   | Hoàng Minh Hải Đăng | Developer               |
| QE200122   | Văn Thái Trung      | Developer               |
| SE201015   | Nguyễn Hoàng Hưng   | Developer               |

---

## 📖 Project Description

The **Bank Transaction History System** is designed to manage bank transactions efficiently using Data Structures and Object-Oriented Programming concepts.

The system allows users to:

* Add new transactions
* Display transaction history
* Search transactions by ID
* Delete transactions
* Undo previous operations
* Generate monthly transaction reports

---

## 🎯 Learning Objectives

This project demonstrates the application of:

* Singly Linked List
* Stack
* Object-Oriented Programming (OOP)
* Searching Algorithms
* Data Management Techniques

---

## 🏗️ System Architecture

### Main Classes

#### Record (Abstract Class)

Base class containing common transaction information.

#### Transaction

Represents a bank transaction including:

* Transaction ID
* Transaction Type
* Amount
* Month
* Description

#### TransactionNode

Node used in the linked list structure.

#### TransactionLinkedList

Manages all transaction records using a Singly Linked List.

#### Action

Stores operation history for Undo functionality.

#### Report (Abstract Class)

Base class for reporting features.

#### ReportManager

Generates monthly transaction reports.

#### BankTransaction

Main application class containing the menu-driven system.

---

## 📊 Data Structures Used

### 1. Singly Linked List

Purpose:
Store transaction history in chronological order.

Operations:

* Insert Transaction
* Delete Transaction
* Traverse Transaction History
* Search by Transaction ID

Time Complexity:

| Operation | Complexity |
| --------- | ---------- |
| Insert    | O(n)       |
| Search    | O(n)       |
| Delete    | O(n)       |
| Display   | O(n)       |

---

### 2. Stack

Purpose:
Support Undo functionality.

Operations:

* Push Action
* Pop Action

Time Complexity:

| Operation | Complexity |
| --------- | ---------- |
| Push      | O(1)       |
| Pop       | O(1)       |

---

### 3. Array

Purpose:
Store monthly transaction statistics.

Implementation:

```java
double[] monthlyTotal = new double[12];
```

Time Complexity:

| Operation | Complexity |
| --------- | ---------- |
| Access    | O(1)       |
| Update    | O(1)       |

---

## 🔄 System Features

### Add Transaction

Users can enter:

* ID
* Type
* Amount
* Month
* Description

The transaction is stored in the linked list and added to the monthly report.

---

### Display Transaction History

Displays all transactions currently stored in the system.

---

### Search Transaction

Search a transaction using its unique ID.

---

### Delete Transaction

Remove a transaction from the linked list.

---

### Undo Operation

Supports undo for:

* Add Transaction
* Delete Transaction

Implemented using Stack.

---

### Monthly Report

Displays total transaction amounts for each month.

Example:

```text
===== MONTHLY REPORT =====
Month 1: 5000.0
Month 2: 3000.0
Month 3: 4500.0
...
```

---

## 📋 Program Menu

```text
====================
BANK TRANSACTION
====================
1. Add Transaction
2. Display History
3. Search By ID
4. Delete Transaction
5. Undo
6. Monthly Report
7. Exit
```

---

## 🔍 UML Overview

Main Relationships:

* Transaction inherits Record
* ReportManager inherits Report
* TransactionLinkedList contains TransactionNode
* TransactionNode contains Transaction
* Action references Transaction
* Stack<Action> supports Undo

---

## ⚙️ Technologies Used

* Java
* NetBeans IDE
* Object-Oriented Programming
* Data Structures & Algorithms

---

## 🚀 Future Improvements

Potential enhancements:

* Transaction sorting by amount/date
* File storage (Save/Load)
* Transaction update feature
* GUI using Java Swing or JavaFX
* Database integration (MySQL)
* Transaction analytics dashboard

---

## 📚 Conclusion

This project demonstrates the practical application of Data Structures and Algorithms in a real-world banking scenario. By combining Linked Lists, Stacks, Arrays, and OOP principles, the system provides efficient transaction management and reporting capabilities.

---

© 2025 - CSD201 Group 2
