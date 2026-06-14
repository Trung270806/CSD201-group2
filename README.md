# Bank Transaction History System (CSD201 Project)

This repository contains the complete Java implementation and empirical study for the **Bank Transaction History System**, matching the strict, revised 10-week academic plan for the Data Structures & Algorithms course (CSD201).

## 🚀 Key Features

1. **Custom Data Structures**:
   - **`CustomHashTable`**: Custom hash table resolved using **Chaining (Buckets)**. Re-allocates and re-hashes elements dynamically when load factor exceeds `0.75` to guarantee average $O(1)$ lookup time.
   - **`CustomSinglyLinkedList`**: Tail-tracked singly linked list for $O(1)$ appends, used as a benchmark baseline.
   - **Fixed 12-element Monthly Array**: Preallocated stat array index-mapped directly to months for $O(1)$ analytics updates.
2. **Strict Banking Rules**:
   - **Audit Trail Compliance**: No deletion/modification operations. Once posted, records remain forever.
   - **Compensatory Reversals**: Reversal transactions balance out human/system errors instead of risky database-altering "undo" operations.
   - **Strict Overdraft Block**: Prevents account balances from falling below zero.
3. **Data Generator**:
   - Generates mock databases with $N \ge 10,000$ to $50,000$ consistent records.
4. **Empirical Study (Benchmark)**:
   - Measures exact execution time in nanoseconds for Custom HashTable vs. Singly Linked List, proving theoretical $O(1)$ vs. $O(n)$ search complexity.
5. **Academic LaTeX Paper**:
   - Standard two-column research paper template prepared to deliver final results.

---

## 🛠️ How to Compile & Run

Open your terminal (PowerShell, Command Prompt, or bash) and navigate to the project directory:

```bash
cd C:\Users\HOANG DANG\.gemini\antigravity-ide\scratch\bank-transaction-system
```

### 1. Compile the Project
```bash
javac -d bin src/Main.java src/com/bank/model/*.java src/com/bank/datastructure/*.java src/com/bank/service/*.java src/com/bank/ui/*.java
```

### 2. Run the Initial Dataset Generator
By default, the project includes 10,000 pre-generated mock transactions. You can re-run the generator directly:
```bash
java -cp bin com.bank.service.DataGenerator
```

### 3. Run the Main System CLI Interface
```bash
java -cp bin Main
```

---

## 📂 Project Directory Structure

- `data/`
  - `accounts.csv`: CSV file storing accounts and current balances.
  - `transactions.csv`: CSV file storing transaction history records.
- `src/`
  - `Main.java`: Bootstrap entry point.
  - `com/bank/model/`: Entity classes (`Transaction.java`, `Account.java`, `TransactionType.java`).
  - `com/bank/datastructure/`: custom-built collections (`CustomHashTable.java`, `CustomSinglyLinkedList.java`).
  - `com/bank/service/`: Business rules, data generator, and benchmark classes.
  - `com/bank/ui/`: Interactive menu console.
- `latex/`
  - `report.tex`: Academic two-column LaTeX document template.
- `README.md`: Guide documentation.
