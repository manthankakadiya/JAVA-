
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Scanner;

record BankDetails(String name, String branch) {
}

enum AccountType {
    SAVINGS, CURRENT, FIXED
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface AccountId {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface PositiveValue {
    String message() default "Value must be positive";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface LengthLimit {
    int value();
}

interface Transaction {
    void deposit(double amount);
    void withdraw(double amount);

    default void showTransactionMessage() {
        System.out.println("MiniBank Transaction System");
    }
}

interface Interest {
    double calculateInterest();

    default void showInterestPolicy() {
        System.out.println("Interest depends on account type.");
    }
}

@FunctionalInterface
interface AccountAction {
    void perform(Account account, double amount);
}

interface Auditable {
}

class Validator {

    static boolean validName(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    static boolean validEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    static boolean validMobile(String mobile) {
        return mobile.matches("[0-9]{10}");
    }

    static String parseCommand(String command) {
        return command.trim().toLowerCase();
    }

    static boolean validateAnnotations(Account account) {

        try {
            Class<?> cls = account.getClass();

            while (cls != null) {

                for (Field field : cls.getDeclaredFields()) {

                    field.setAccessible(true);
                    Object value = field.get(account);

                    if (field.isAnnotationPresent(AccountId.class)) {
                        System.out.println("Account ID Found: " + value);
                    }

                    if (field.isAnnotationPresent(PositiveValue.class)) {

                        PositiveValue annotation =
                                field.getAnnotation(PositiveValue.class);

                        if (value instanceof Number &&
                                ((Number) value).doubleValue() <= 0) {

                            System.out.println(annotation.message());
                            return false;
                        }
                    }

                    if (field.isAnnotationPresent(LengthLimit.class)) {

                        LengthLimit annotation =
                                field.getAnnotation(LengthLimit.class);

                        if (value instanceof String &&
                                ((String) value).length() > annotation.value()) {

                            System.out.println("Maximum length is "
                                    + annotation.value());
                            return false;
                        }
                    }
                }

                cls = cls.getSuperclass();
            }

            return true;

        } catch (Exception e) {
            System.out.println("Validation Error");
            return false;
        }
    }
}

abstract class Account implements Transaction, Interest,
        Auditable, Cloneable {

    @AccountId
    private String accountNumber;

    @LengthLimit(30)
    private String holderName;

    private String email;
    private String mobile;

    @PositiveValue(message = "Balance must be positive")
    protected double balance;

    private AccountType type;

    private final String bankName = "MiniBank";

    private static int totalAccounts = 0;

    Account(String accountNumber, String holderName,
            String email, String mobile, double balance,
            AccountType type) {

        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.email = email;
        this.mobile = mobile;
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

    public static int getTotalAccounts() {
        return totalAccounts;
    }

    @Override
    public void deposit(double amount) {

        if (amount > 0) {
            balance += amount;
            System.out.println("Deposit Successful");
        } else {
            System.out.println("Invalid Amount");
        }
    }

    @Override
    public void withdraw(double amount) {

        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawal Successful");
        } else {
            System.out.println("Insufficient Balance or Invalid Amount");
        }
    }

    @Override
    public abstract double calculateInterest();

    @Override
    public String toString() {

        StringBuilder details = new StringBuilder();

        details.append("\nBank Name: ").append(bankName);
        details.append("\nAccount Number: ").append(accountNumber);
        details.append("\nHolder Name: ").append(holderName);
        details.append("\nEmail: ").append(email);
        details.append("\nMobile: ").append(mobile);
        details.append("\nAccount Type: ").append(type);
        details.append("\nBalance: Rs. ").append(balance);
        details.append("\nInterest: Rs. ").append(calculateInterest());

        return details.toString();
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

class SavingsAccount extends Account {

    SavingsAccount(String number, String name, String email,
                   String mobile, double balance) {

        super(number, name, email, mobile, balance,
                AccountType.SAVINGS);
    }

    @Override
    public double calculateInterest() {
        return balance * 4 / 100;
    }
}

class CurrentAccount extends Account {

    private final double overdraftLimit = 5000;

    CurrentAccount(String number, String name, String email,
                   String mobile, double balance) {

        super(number, name, email, mobile, balance,
                AccountType.CURRENT);
    }

    @Override
    public double calculateInterest() {
        return 0;
    }

    @Override
    public void withdraw(double amount) {

        if (amount > 0 && amount <= balance + overdraftLimit) {
            balance -= amount;
            System.out.println("Withdrawal Successful");
        } else {
            System.out.println("Overdraft Limit Exceeded");
        }
    }
}

class FixedAccount extends Account {

    FixedAccount(String number, String name, String email,
                 String mobile, double balance) {

        super(number, name, email, mobile, balance,
                AccountType.FIXED);
    }

    @Override
    public double calculateInterest() {
        return balance * 7 / 100;
    }

    @Override
    public void withdraw(double amount) {
        System.out.println("Fixed Account withdrawal is not allowed");
    }
}

public class BankManagement {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankDetails bank = new BankDetails(
                "MiniBank", "CHARUSAT Branch");

        Account account = null;
        Account clonedAccount = null;

        AccountAction depositAction =
                (acc, amount) -> acc.deposit(amount);

        AccountAction withdrawAction =
                (acc, amount) -> acc.withdraw(amount);

        boolean running = true;

        System.out.println("Welcome to " + bank.name());

        Account.BankBranch branch = new Account.BankBranch();
        branch.displayBranch();

        while (running) {

            System.out.println("\nCommands:");
            System.out.println("open");
            System.out.println("deposit");
            System.out.println("withdraw");
            System.out.println("show");
            System.out.println("interest");
            System.out.println("total");
            System.out.println("clone");
            System.out.println("compare");
            System.out.println("exit");

            System.out.print("\nEnter Command: ");
            String command = Validator.parseCommand(sc.nextLine());

            String[] parts = command.split("\\s+");
            String action = parts[0];

            switch (action) {

                case "open":

                    System.out.print("Enter Account Number: ");
                    String number = sc.nextLine();

                    System.out.print("Enter Holder Name: ");
                    String name = sc.nextLine();

                    if (!Validator.validName(name)) {
                        System.out.println("Invalid Name");
                        break;
                    }

                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();

                    if (!Validator.validEmail(email)) {
                        System.out.println("Invalid Email");
                        break;
                    }

                    System.out.print("Enter Mobile Number: ");
                    String mobile = sc.nextLine();

                    if (!Validator.validMobile(mobile)) {
                        System.out.println("Invalid Mobile Number");
                        break;
                    }

                    System.out.print("Enter Initial Balance: ");
                    double amount = Double.parseDouble(sc.nextLine());

                    System.out.println("1. Savings");
                    System.out.println("2. Current");
                    System.out.println("3. Fixed");

                    System.out.print("Choose Account Type: ");
                    int type = Integer.parseInt(sc.nextLine());

                    switch (type) {

                        case 1:
                            account = new SavingsAccount(
                                    number, name, email, mobile, amount);
                            break;

                        case 2:
                            account = new CurrentAccount(
                                    number, name, email, mobile, amount);
                            break;

                        case 3:
                            account = new FixedAccount(
                                    number, name, email, mobile, amount);
                            break;

                        default:
                            System.out.println("Invalid Account Type");
                            continue;
                    }

                    if (!Validator.validateAnnotations(account)) {
                        account = null;
                        System.out.println("Account Validation Failed");
                        break;
                    }

                    clonedAccount = null;
                    System.out.println("Account Created Successfully");
                    break;

                case "deposit":

                    if (account != null) {
                        System.out.print("Enter Deposit Amount: ");
                        double deposit = Double.parseDouble(sc.nextLine());

                        account.showTransactionMessage();
                        depositAction.perform(account, deposit);
                    } else {
                        System.out.println("Open Account First");
                    }
                    break;

                case "withdraw":

                    if (account != null) {
                        System.out.print("Enter Withdrawal Amount: ");
                        double withdraw = Double.parseDouble(sc.nextLine());

                        account.showTransactionMessage();
                        withdrawAction.perform(account, withdraw);
                    } else {
                        System.out.println("Open Account First");
                    }
                    break;

                case "show":

                    if (account != null) {
                        System.out.println(account);
                    } else {
                        System.out.println("No Account Found");
                    }
                    break;

                case "interest":

                    if (account != null) {
                        account.showInterestPolicy();
                        System.out.println("Interest: Rs. "
                                + account.calculateInterest());
                    } else {
                        System.out.println("Open Account First");
                    }
                    break;

                case "total":

                    System.out.println("Total Accounts: "
                            + Account.getTotalAccounts());
                    break;

                case "clone":

                    if (account != null) {
                        clonedAccount = account.clone();
                        System.out.println("Account Cloned Successfully");
                    } else {
                        System.out.println("Open Account First");
                    }
                    break;

                case "compare":

                    if (account != null && clonedAccount != null) {
                        System.out.println("Accounts Equal: "
                                + account.equals(clonedAccount));
                        System.out.println("HashCode: "
                                + account.hashCode());
                    } else {
                        System.out.println("Create and Clone Account First");
                    }
                    break;

                case "exit":

                    running = false;
                    System.out.println("Thank You for Using MiniBank");
                    break;

                default:

                    System.out.println("Invalid Command");
            }

            if (account instanceof Auditable) {
                System.out.println("Account supports auditing.");
            }
        }

        sc.close();
    }
}
