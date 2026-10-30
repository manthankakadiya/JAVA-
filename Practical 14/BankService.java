
package service;

import java.io.*;
import java.util.ArrayList;
import model.BankAccount;

public class BankService {

    private ArrayList<BankAccount> accounts = new ArrayList<>();

    public void addAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        accounts.add(account);
    }

    public int getCount() {
        return accounts.size();
    }

    public BankAccount getAccount(int index) {
        if (index < 0 || index >= accounts.size()) {
            throw new IllegalArgumentException("Invalid account index");
        }

        return accounts.get(index);
    }

    public void saveData(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(filename))) {

            out.writeObject(accounts);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData(String filename)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(filename))) {

            accounts = (ArrayList<BankAccount>) in.readObject();
        }
    }
}
