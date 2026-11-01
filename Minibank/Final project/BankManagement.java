
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class BankManagement {

    record BankDetails(String name, String branch) {
    }

    enum AccountType {
        SAVINGS, CURRENT, FIXED
    }

    enum MenuChoice {
        OPEN, DEPOSIT, WITHDRAW, TRANSFER, SHOW, EXIT
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

    static class BankException extends Exception {
        BankException(String message) {
            super(message);
        }
    }

    interface Transaction {
        void deposit(long amount) throws BankException;

        boolean withdraw(long amount) throws BankException;
    }

    interface Interest {
        double getRate();

        default double calculateInterest(long balance) {
            return balance * getRate() / 100.0;
        }
    }

    static abstract class Account implements Transaction,
            Interest, Serializable, Comparable<Account> {

        private static final long serialVersionUID = 1L;

        @AccountId
        private String accountNumber;

        @LengthLimit(30)
        private String holderName;

        @PositiveValue
        protected long balance;

        private AccountType type;

        Account(String number, String name, long amount,
                AccountType type) {
            accountNumber = number;
            holderName = name;
            balance = amount;
            this.type = type;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getHolderName() {
            return holderName;
        }

        public long getBalance() {
            return balance;
        }

        public AccountType getType() {
            return type;
        }

        @Override
        public void deposit(long amount) throws BankException {
            if (amount <= 0) {
                throw new BankException("Invalid deposit amount");
            }
            balance += amount;
        }

        @Override
        public boolean withdraw(long amount) throws BankException {
            if (amount <= 0) {
                throw new BankException("Invalid withdrawal amount");
            }

            if (amount > balance) {
                throw new BankException("Insufficient balance");
            }

            balance -= amount;
            return true;
        }

        @Override
        public int compareTo(Account other) {
            return Long.compare(balance, other.balance);
        }

        @Override
        public String toString() {
            return "Account Number: " + accountNumber
                    + "\nHolder Name: " + holderName
                    + "\nType: " + type
                    + "\nBalance: Rs. " + balance;
        }
    }

    static class SavingsAccount extends Account {

        SavingsAccount(String number, String name, long amount) {
            super(number, name, amount, AccountType.SAVINGS);
        }

        @Override
        public double getRate() {
            return 4.0;
        }
    }

    static class CurrentAccount extends Account {

        private final long overdraftLimit = 5000;

        CurrentAccount(String number, String name, long amount) {
            super(number, name, amount, AccountType.CURRENT);
        }

        @Override
        public boolean withdraw(long amount) throws BankException {
            if (amount <= 0) {
                throw new BankException("Invalid amount");
            }

            if (amount > getBalance() + overdraftLimit) {
                throw new BankException("Overdraft limit exceeded");
            }

            balance -= amount;
            return true;
        }

        @Override
        public double getRate() {
            return 0.0;
        }
    }

    static class FixedAccount extends Account {

        private LocalDate maturityDate;

        FixedAccount(String number, String name, long amount) {
            super(number, name, amount, AccountType.FIXED);
            maturityDate = LocalDate.now().plusYears(1);
        }

        @Override
        public boolean withdraw(long amount) throws BankException {
            if (LocalDate.now().isBefore(maturityDate)) {
                throw new BankException("Account is not matured yet");
            }
            return super.withdraw(amount);
        }

        @Override
        public double getRate() {
            return 7.0;
        }
    }

    static class Validator {

        static boolean validName(String name) {
            return name != null && name.matches("[a-zA-Z ]+");
        }

        static boolean validEmail(String email) {
            return Pattern.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
                    email);
        }

        static boolean validMobile(String mobile) {
            return mobile.matches("[0-9]{10}");
        }

        static void checkFields(Object object)
                throws BankException {

            try {
                for (Field field : object.getClass()
                        .getSuperclass().getDeclaredFields()) {

                    field.setAccessible(true);

                    if (field.isAnnotationPresent(PositiveValue.class)) {
                        if (field.getLong(object) <= 0) {
                            PositiveValue annotation = field.getAnnotation(PositiveValue.class);
                            throw new BankException(annotation.message());
                        }
                    }

                    if (field.isAnnotationPresent(LengthLimit.class)) {
                        Object value = field.get(object);

                        if (value instanceof String) {
                            int limit = field.getAnnotation(
                                    LengthLimit.class).value();

                            if (((String) value).length() > limit) {
                                throw new BankException(
                                        "Name is too long");
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new BankException("Validation failed");
            }
        }
    }

    interface Repository<T> {
        void save(T item);

        T find(String id);

        List<T> getAll();
    }

    static class AccountStore implements Repository<Account> {

        private final Map<String, Account> accounts = new ConcurrentHashMap<>();

        private final List<String> history = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void save(Account account) {
            accounts.put(account.getAccountNumber(), account);
        }

        @Override
        public Account find(String number) {
            return accounts.get(number);
        }

        @Override
        public List<Account> getAll() {
            return new ArrayList<>(accounts.values());
        }

        public void addHistory(String message) {
            history.add(LocalDate.now() + " : " + message);
        }

        public void showHistory() {
            synchronized (history) {
                for (String item : history) {
                    System.out.println(item);
                }
            }
        }
    }

    static class FileManager {

        private static final String DATA_FILE = "bankdata.dat";
        private static final String LOG_FILE = "banklog.txt";

        static void saveAccounts(List<Account> accounts) {
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(DATA_FILE))) {

                out.writeObject(new ArrayList<>(accounts));
                System.out.println("Data saved successfully.");

            } catch (IOException e) {
                System.out.println("Error saving data: " + e.getMessage());
            }
        }

        @SuppressWarnings("unchecked")
        static List<Account> loadAccounts() {
            File file = new File(DATA_FILE);

            if (!file.exists()) {
                return new ArrayList<>();
            }

            try (ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(file))) {

                return (List<Account>) in.readObject();

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Could not load previous data.");
                return new ArrayList<>();
            }
        }

        static void writeLog(String message) {
            try {
                Files.write(
                        Paths.get(LOG_FILE),
                        (message + System.lineSeparator())
                                .getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println("Log error: " + e.getMessage());
            }
        }
    }

    static class BankService {

        private final AccountStore store;
        private int nextNumber = 1000;

        BankService(AccountStore store) {
            this.store = store;

            for (Account account : store.getAll()) {
                try {
                    int number = Integer.parseInt(
                            account.getAccountNumber().substring(2));

                    if (number >= nextNumber) {
                        nextNumber = number + 1;
                    }
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    // Invalid saved account number
                }
            }
        }

        public void openAccount(String name, long amount,
                AccountType type) throws BankException {

            if (!Validator.validName(name)) {
                throw new BankException("Invalid account holder name");
            }

            if (amount <= 0) {
                throw new BankException("Initial amount must be positive");
            }

            String number = "AC" + nextNumber++;

            Account account;

            switch (type) {
                case SAVINGS:
                    if (amount < 500) {
                        throw new BankException(
                                "Savings account requires minimum Rs. 500");
                    }
                    account = new SavingsAccount(number, name, amount);
                    break;

                case CURRENT:
                    account = new CurrentAccount(number, name, amount);
                    break;

                case FIXED:
                    account = new FixedAccount(number, name, amount);
                    break;

                default:
                    throw new BankException("Invalid account type");
            }

            Validator.checkFields(account);
            store.save(account);
            record("New account opened: " + number);

            System.out.println("Account created successfully!");
            System.out.println("Your account number is: " + number);
        }

        public void deposit(String number, long amount)
                throws BankException {

            Account account = getAccount(number);
            account.deposit(amount);

            record("Deposited Rs. " + amount + " into " + number);
            System.out.println("Deposit successful.");
        }

        public void withdraw(String number, long amount)
                throws BankException {

            Account account = getAccount(number);
            account.withdraw(amount);

            record("Withdrawn Rs. " + amount + " from " + number);
            System.out.println("Withdrawal successful.");
        }

        public void transfer(String from, String to, long amount)
                throws BankException {

            Account sender = getAccount(from);
            Account receiver = getAccount(to);

            if (amount <= 0) {
                throw new BankException("Invalid transfer amount");
            }

            sender.withdraw(amount);

            try {
                receiver.deposit(amount);
            } catch (BankException e) {
                sender.deposit(amount);
                throw e;
            }

            record("Transferred Rs. " + amount
                    + " from " + from + " to " + to);

            System.out.println("Transfer successful.");
        }

        public Account getAccount(String number)
                throws BankException {

            Account account = store.find(number);

            if (account == null) {
                throw new BankException("Account not found");
            }

            return account;
        }

        public void showAccounts() {
            List<Account> list = store.getAll();
            list.sort(Comparator.comparing(Account::getHolderName));

            if (list.isEmpty()) {
                System.out.println("No accounts available.");
                return;
            }

            for (Account account : list) {
                System.out.println("----------------------");
                System.out.println(account);
                System.out.println("Interest: Rs. "
                        + account.calculateInterest(account.getBalance()));
            }
        }

        public void batchDeposit(String number, long amount, int count) {

            ExecutorService executor = Executors.newFixedThreadPool(4);

            for (int i = 0; i < count; i++) {
                executor.submit(() -> {
                    try {
                        deposit(number, amount);
                    } catch (BankException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }

            executor.shutdown();

            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        private void record(String message) {
            store.addHistory(message);
            FileManager.writeLog(message);
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankDetails bank = new BankDetails(
                "MiniBank", "CHARUSAT Branch");

        AccountStore store = new AccountStore();

        for (Account account : FileManager.loadAccounts()) {
            store.save(account);
        }

        BankService service = new BankService(store);

        System.out.println("================================");
        System.out.println("       " + bank.name());
        System.out.println("       " + bank.branch());
        System.out.println("================================");

        boolean running = true;

        while (running) {

            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. Show Accounts");
            System.out.println("6. Transaction History");
            System.out.println("7. Batch Deposit");
            System.out.println("8. Save and Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:
                        System.out.print("Enter holder name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter initial amount: ");
                        long amount = sc.nextLong();
                        sc.nextLine();

                        System.out.println("1. Savings");
                        System.out.println("2. Current");
                        System.out.println("3. Fixed");
                        System.out.print("Choose account type: ");

                        int type = sc.nextInt();
                        sc.nextLine();

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
                                throw new BankException("Invalid account type");
                        }

                        service.openAccount(name, amount, accountType);
                        break;

                    case 2:
                        System.out.print("Enter account number: ");
                        String depositNumber = sc.nextLine();

                        System.out.print("Enter amount: ");
                        long depositAmount = sc.nextLong();
                        sc.nextLine();

                        service.deposit(depositNumber, depositAmount);
                        break;

                    case 3:
                        System.out.print("Enter account number: ");
                        String withdrawNumber = sc.nextLine();

                        System.out.print("Enter amount: ");
                        long withdrawAmount = sc.nextLong();
                        sc.nextLine();

                        service.withdraw(withdrawNumber, withdrawAmount);
                        break;

                    case 4:
                        System.out.print("Enter sender account: ");
                        String sender = sc.nextLine();

                        System.out.print("Enter receiver account: ");
                        String receiver = sc.nextLine();

                        System.out.print("Enter amount: ");
                        long transferAmount = sc.nextLong();
                        sc.nextLine();

                        service.transfer(sender, receiver, transferAmount);
                        break;

                    case 5:
                        service.showAccounts();
                        break;

                    case 6:
                        store.showHistory();
                        break;

                    case 7:
                        System.out.print("Enter account number: ");
                        String batchNumber = sc.nextLine();

                        System.out.print("Enter amount per deposit: ");
                        long batchAmount = sc.nextLong();

                        System.out.print("Enter number of deposits: ");
                        int count = sc.nextInt();
                        sc.nextLine();

                        if (count <= 0) {
                            throw new BankException("Invalid deposit count");
                        }

                        service.batchDeposit(
                                batchNumber, batchAmount, count);
                        break;

                    case 8:
                        FileManager.saveAccounts(store.getAll());
                        System.out.println("Thank you for using MiniBank!");
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();

            } catch (BankException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        sc.close();
    }
}
