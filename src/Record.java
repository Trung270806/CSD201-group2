/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BankTransaction;

public abstract class Record {

    protected int id;

    public Record(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public abstract void display();
}