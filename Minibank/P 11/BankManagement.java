
import java.util.Scanner;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

class BankException extends Exception {
    BankException(String message) {
        super(message);
    }
}

interface Transaction {
    void deposit(double amount) throws BankException;
    void withdraw(double amount) throws BankException;

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
    void perform(Account account, double amount) throws BankException;
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
                                ((Number) value).doubleValue() < 0) {
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
        Auditable, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    @AccountId
    private String accountNumber;

    @LengthLimit(30)
    private String holderName;

    private String email;
    private String mobile;

    @PositiveValue(message = "Balance cannot be negative")
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

    public synchronized double getBalance() {
        return balance;
    }

    public AccountType getType() {
        return type;
    }

    public static int getTotalAccounts() {
        return totalAccounts;
    }

    @Override
    public synchronized void deposit(double amount)
            throws BankException {

        if (amount <= 0) {
            throw new BankException("Deposit amount must be positive");
        }

        balance += amount;

        System.out.println(Thread.currentThread().getName()
                + " deposited Rs. " + amount);
    }

    @Override
    public synchronized void withdraw(double amount)
            throws BankException {

        if (amount <= 0) {
            throw new BankException("Withdrawal amount must be positive");
        }

        if (amount > balance) {
            throw new BankException("Insufficient Balance");
        }

        balance -= amount;

        System.out.println(Thread.currentThread().getName()
                + " withdrew Rs. " + amount);
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
        details.append("\nBalance: Rs. ").append(getBalance());
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

    private static final long serialVersionUID = 1L;

    SavingsAccount(String number, String name, String email,
                   String mobile, double balance) {
        super(number, name, email, mobile, balance,
                AccountType.SAVINGS);
    }

    @Override
    public double calculateInterest() {
        return getBalance() * 4 / 100;
    }
}

class CurrentAccount extends Account {

    private static final long serialVersionUID = 1L;

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
    public synchronized void withdraw(double amount)
            throws BankException {

        if (amount <= 0) {
            throw new BankException("Amount must be positive");
        }

        if (amount > balance + overdraftLimit) {
            throw new BankException("Overdraft Limit Exceeded");
        }

        balance -= amount;

        System.out.println("Withdrawal Successful");
    }
}

class FixedAccount extends Account {

    private static final long serialVersionUID = 1L;

    FixedAccount(String number, String name, String email,
                 String mobile, double balance) {
        super(number, name, email, mobile, balance,
                AccountType.FIXED);
    }

    @Override
    public double calculateInterest() {
        return getBalance() * 7 / 100;
    }

    @Override
    public void withdraw(double amount) throws BankException {
        throw new BankException(
                "Fixed Account withdrawal is not allowed");
    }
}

class FileManager {

    private static final String ACCOUNT_FILE = "data/accounts.dat";
    private static final String LOG_FILE = "logs/transactions.log";

    static void saveAccount(Account account) throws IOException {

        Files.createDirectories(Paths.get("data"));

        try (ObjectOutputStream output =
                     new ObjectOutputStream(
                             new FileOutputStream(ACCOUNT_FILE))) {

            output.writeObject(account);
        }

        writeLog("Account saved: " + account.getAccountNumber());

        System.out.println("Account Saved Successfully");
    }

    static Account loadAccount()
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream input =
                     new ObjectInputStream(
                             new FileInputStream(ACCOUNT_FILE))) {

            Account account = (Account) input.readObject();

            writeLog("Account loaded: " + account.getAccountNumber());

            return account;
        }
    }

    static void writeLog(String message) throws IOException {

        Files.createDirectories(Paths.get("logs"));

        String logMessage = java.time.LocalDateTime.now()
                + " - " + message + System.lineSeparator();

        Files.writeString(
                Paths.get(LOG_FILE),
                logMessage,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    static void showLogs() throws IOException {

        Path path = Paths.get(LOG_FILE);

        if (!Files.exists(path)) {
            System.out.println("No Transaction Log Found");
            return;
        }

        System.out.println("\nTransaction History:");

        for (String line : Files.readAllLines(path)) {
            System.out.println(line);
        }
    }
}

class BankTask implements Runnable {

    private Account account;
    private double amount;

    BankTask(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            account.deposit(amount);

            FileManager.writeLog(
                    "Thread deposit: Rs. " + amount
                    + " | Account: " + account.getAccountNumber()
            );

        } catch (BankException e) {
            System.out.println(e.getMessage());

        } catch (IOException e) {
            System.out.println("Unable to write transaction log");
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
            System.out.println("thread");
            System.out.println("pool");
            System.out.println("save");
            System.out.println("load");
            System.out.println("log");
            System.out.println("exit");

            System.out.print("\nEnter Command: ");
            String command = Validator.parseCommand(sc.nextLine());

            String action = command.split("\\s+")[0];

            try {
                switch (action) {

                    case "open": {
                        System.out.print("Enter Account Number: ");
                        String number = sc.nextLine();

                        System.out.print("Enter Holder Name: ");
                        String name = sc.nextLine();

                        if (!Validator.validName(name)) {
                            throw new BankException("Invalid Name");
                        }

                        System.out.print("Enter Email: ");
                        String email = sc.nextLine();

                        if (!Validator.validEmail(email)) {
                            throw new BankException("Invalid Email");
                        }

                        System.out.print("Enter Mobile Number: ");
                        String mobile = sc.nextLine();

                        if (!Validator.validMobile(mobile)) {
                            throw new BankException("Invalid Mobile Number");
                        }

                        System.out.print("Enter Initial Balance: ");
                        double amount = Double.parseDouble(sc.nextLine());

                        if (amount < 0) {
                            throw new BankException("Invalid Balance");
                        }

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
                                throw new BankException("Invalid Account Type");
                        }

                        if (!Validator.validateAnnotations(account)) {
                            account = null;
                            throw new BankException("Account Validation Failed");
                        }

                        clonedAccount = null;

                        FileManager.writeLog(
                                "New account created: " + account.getAccountNumber()
                        );

                        System.out.println("Account Created Successfully");
                        break;
                    }

                    case "deposit": {
                        if (account == null) {
                            throw new BankException("Open Account First");
                        }

                        System.out.print("Enter Deposit Amount: ");
                        double amount = Double.parseDouble(sc.nextLine());

                        account.showTransactionMessage();
                        depositAction.perform(account, amount);

                        FileManager.writeLog(
                                "Deposited Rs. " + amount
                                + " | Account: " + account.getAccountNumber()
                        );
                        break;
                    }

                    case "withdraw": {
                        if (account == null) {
                            throw new BankException("Open Account First");
                        }

                        System.out.print("Enter Withdrawal Amount: ");
                        double amount = Double.parseDouble(sc.nextLine());

                        account.showTransactionMessage();
                        withdrawAction.perform(account, amount);

                        FileManager.writeLog(
                                "Withdrawn Rs. " + amount
                                + " | Account: " + account.getAccountNumber()
                        );
                        break;
                    }

                    case "show": {
                        if (account == null) {
                            throw new BankException("No Account Found");
                        }

                        System.out.println(account);
                        break;
                    }

                    case "interest": {
                        if (account == null) {
                            throw new BankException("Open Account First");
                        }

                        account.showInterestPolicy();
                        System.out.println("Interest: Rs. "
                                + account.calculateInterest());
                        break;
                    }

                    case "total": {
                        System.out.println("Total Accounts: "
                                + Account.getTotalAccounts());
                        break;
                    }

                    case "clone": {
                        if (account == null) {
                            throw new BankException("Open Account First");
                        }

                        clonedAccount = account.clone();
                        System.out.println("Account Cloned Successfully");
                        break;
                    }

                    case "compare": {
                        if (account == null || clonedAccount == null) {
                            throw new BankException(
                                    "Create and Clone Account First");
                        }

                        System.out.println("Accounts Equal: "
                                + account.equals(clonedAccount));
                        break;
                    }

                    case "thread": {
                        if (account == null) {
                            throw new BankException("Open Account First");
                        }

                        Thread t1 = new Thread(
                                new BankTask(account, 1000), "Thread-1");

                        Thread t2 = new Thread(
                                new BankTask(account, 2000), "Thread-2");

                        t1.start();
                        t2.start();

                        t1.join();
                        t2.join();

                        System.out.println("Both Threads Completed");
                        System.out.println("Final Balance: Rs. "
                                + account.getBalance());
                        break;
                    }

                    case "pool": {
                        if (account == null) {
                            throw new BankException("Open Account First");
                        }

                        ExecutorService executor =
                                Executors.newFixedThreadPool(3);

                        executor.submit(new BankTask(account, 500));
                        executor.submit(new BankTask(account, 1000));
                        executor.submit(new BankTask(account, 1500));
                        executor.submit(new BankTask(account, 2000));

                        executor.shutdown();

                        System.out.println("Tasks Submitted Successfully");

                        if (executor.awaitTermination(
                                10, TimeUnit.SECONDS)) {

                            System.out.println("All Tasks Completed");

                        } else {
                            System.out.println("Tasks Taking Too Long");
                        }

                        System.out.println("Final Balance: Rs. "
                                + account.getBalance());
                        break;
                    }

                    case "save": {
                        if (account == null) {
                            throw new BankException("Open Account First");
                        }

                        FileManager.saveAccount(account);
                        break;
                    }

                    case "load": {
                        account = FileManager.loadAccount();
                        clonedAccount = null;

                        System.out.println("Account Loaded Successfully");
                        System.out.println(account);
                        break;
                    }

                    case "log": {
                        FileManager.showLogs();
                        break;
                    }

                    case "exit": {
                        running = false;
                        System.out.println("Thank You for Using MiniBank");
                        break;
                    }

                    default:
                        throw new BankException("Invalid Command");
                }

            } catch (BankException e) {
                System.out.println("Bank Error: " + e.getMessage());

            } catch (NumberFormatException e) {
                System.out.println("Please Enter a Valid Number");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread Interrupted");

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());

            } finally {
                System.out.println("Operation Completed");
            }

            if (account instanceof Auditable) {
                System.out.println("Account supports auditing.");
            }
        }

        sc.close();
    }
}
