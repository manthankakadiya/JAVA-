
import java.util.Scanner;

record BankDetails(String name, String branch) {
}

enum AccountType {
    SAVINGS, CURRENT, FIXED
}

class Account implements Cloneable {

    private String accountNumber;
    private String holderName;
    private double balance;
    private AccountType type;

    private final String bankName = "MiniBank";

    private static int totalAccounts = 0;

    Account(String accountNumber, String holderName,
            double balance, AccountType type) {

        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.type = type;

        totalAccounts++;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public AccountType getType() {
        return type;
    }

    public String getBankName() {
        return bankName;
    }

    public static int getTotalAccounts() {
        return totalAccounts;
    }

    void deposit(double amount) {
        if (amount > 0) {
            balance = balance + amount;
            System.out.println("Deposit Successful");
        } else {
            System.out.println("Invalid Amount");
        }
    }

    @Override
    public String toString() {
        return "Bank: " + bankName +
                "\nAccount Number: " + accountNumber +
                "\nHolder Name: " + holderName +
                "\nAccount Type: " + type +
                "\nBalance: Rs. " + balance;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Account other = (Account) obj;

        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }

    @Override
    public Account clone() {
        try {
            return (Account) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    static class BankBranch {

        void displayBranch() {
            System.out.println("Branch: CHARUSAT Branch");
        }
    }
}

public class BankManagement {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankDetails bank = new BankDetails(
                "MiniBank", "CHARUSAT Branch");

        Account account = null;
        Account clonedAccount = null;

        boolean running = true;

        System.out.println("Welcome to " + bank.name());

        Account.BankBranch branch = new Account.BankBranch();
        branch.displayBranch();

        while (running) {

            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Show Account");
            System.out.println("4. Total Accounts");
            System.out.println("5. Clone Account");
            System.out.println("6. Compare Accounts");
            System.out.println("7. Exit");

            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:

                    System.out.print("Enter Account Number: ");
                    String number = sc.nextLine();

                    System.out.print("Enter Holder Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Initial Balance: ");
                    double amount = sc.nextDouble();

                    System.out.println("1. Savings");
                    System.out.println("2. Current");
                    System.out.println("3. Fixed");

                    System.out.print("Choose Account Type: ");
                    int type = sc.nextInt();

                    AccountType accountType;

                    switch (type) {
                        case 1:
                            accountType = AccountType.SAVINGS;
                            break;
                        case 2:
                            accountType = AccountType.CURRENT;
                            break;
                        case 3:
                            accountType = AccountType.FIXED;
                            break;
                        default:
                            System.out.println("Invalid Account Type");
                            continue;
                    }

                    account = new Account(
                            number, name, amount, accountType);

                    System.out.println("Account Created Successfully");
                    break;

                case 2:

                    if (account != null) {
                        System.out.print("Enter Deposit Amount: ");
                        double deposit = sc.nextDouble();
                        account.deposit(deposit);
                    } else {
                        System.out.println("Open Account First");
                    }
                    break;

                case 3:

                    if (account instanceof Account) {
                        System.out.println(account);
                    } else {
                        System.out.println("No Account Found");
                    }
                    break;

                case 4:

                    System.out.println("Total Accounts: "
                            + Account.getTotalAccounts());
                    break;

                case 5:

                    if (account != null) {
                        clonedAccount = account.clone();
                        System.out.println("Account Cloned Successfully");
                        System.out.println(clonedAccount);
                    } else {
                        System.out.println("Open Account First");
                    }
                    break;

                case 6:

                    if (account != null && clonedAccount != null) {

                        System.out.println("Accounts Equal: "
                                + account.equals(clonedAccount));

                        System.out.println("Account HashCode: "
                                + account.hashCode());

                        System.out.println("Clone HashCode: "
                                + clonedAccount.hashCode());

                    } else {
                        System.out.println("Create and Clone Account First");
                    }
                    break;

                case 7:

                    running = false;
                    System.out.println("Thank You for Using MiniBank");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }
        }

        sc.close();
    }
}
