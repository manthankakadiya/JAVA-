
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AccountService service = new AccountService();

        int choice;

        do {
            System.out.println("\n1. Add Account");
            System.out.println("2. View Account Count");
            System.out.println("3. Exit");

            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter balance: ");
                    double balance = sc.nextDouble();

                    service.addAccount(new Account(name, balance));

                    System.out.println("Account added!");
                    break;

                case 2:
                    System.out.println("Total accounts: " + service.getCount());
                    break;

                case 3:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 3);

        sc.close();
    }
}
