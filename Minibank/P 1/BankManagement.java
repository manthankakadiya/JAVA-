import java.util.Scanner;

record BankDetails(String name, String branch) {
}

enum AccountType {
    SAVINGS, CURRENT, FIXED
}

class Account {

    private String accountNumber;
    private String holderName;
    private double balance;
    private AccountType type;

    Account(String accountNumber, String holderName,
            double balance, AccountType type) {

        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.type = type;
    }

    void deposit(double amount) {
        if (amount > 0) {
            balance = balance + amount;
            System.out.println("Deposit Successful");
        } else {
            System.out.println("Invalid Amount");
        }
    }

    void displayAccount() {
        System.out.println("\nAccount Number: " + accountNumber);
        System.out.println("Holder Name: " + holderName);
        System.out.println("Account Type: " + type);
        System.out.println("Balance: Rs. " + balance);
    }
}

public class BankManagement {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankDetails bank = new BankDetails(
                "MiniBank", "CHARUSAT Branch");

        Account account = null;

        boolean running = true;

        System.out.println("Welcome to " + bank.name());
        System.out.println(bank.branch());

        while (running) {

            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Show Account");
            System.out.println("4. Exit");

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

                    if (account != null) {
                        account.displayAccount();
                    } else {
                        System.out.println("No Account Found");
                    }
                    break;

                case 4:

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