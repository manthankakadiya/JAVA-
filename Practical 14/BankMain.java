
package app;

import java.util.Scanner;
import model.BankAccount;
import service.BankService;

public class BankMain {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BankService service = new BankService();

        int choice;

        do {
            System.out.println("\n1. Add Account");
            System.out.println("2. View Account Count");
            System.out.println("3. Save Data");
            System.out.println("4. Load Data");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {

                    case 1:
                        System.out.print("Enter name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter balance: ");
                        double balance = sc.nextDouble();

                        service.addAccount(new BankAccount(name, balance));

                        System.out.println("Account added!");
                        break;

                    case 2:
                        System.out.println(
                            "Total accounts: " + service.getCount()
                        );
                        break;

                    case 3:
                        service.saveData("accounts.dat");
                        System.out.println("Data saved!");
                        break;

                    case 4:
                        service.loadData("accounts.dat");
                        System.out.println("Data loaded!");
                        break;

                    case 5:
                        System.out.println("Exiting...");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (choice != 5);

        sc.close();
    }
}
