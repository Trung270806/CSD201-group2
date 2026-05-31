/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BankTransaction;

public class Action {

    String actionType;
    Transaction transaction;

    public Action(String actionType,
            Transaction transaction) {

        this.actionType = actionType;
        this.transaction = transaction;
    }
}