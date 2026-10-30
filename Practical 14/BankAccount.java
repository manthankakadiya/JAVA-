
package model;

import java.io.Serializable;

public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private double balance;

    public BankAccount(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }
}
