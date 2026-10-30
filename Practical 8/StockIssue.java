
import java.util.Scanner;

class OutOfStockException extends Exception {
    private int shortage;

    public OutOfStockException(int shortage) {
        super("Out of stock! Shortage: " + shortage);
        this.shortage = shortage;
    }

    public int getShortage() {
        return shortage;
    }
}

class InvalidQuantityException extends Exception {
    public InvalidQuantityException() {
        super("Invalid quantity! Must be greater than 0.");
    }
}

class Warehouse {
    private int stock;

    public Warehouse(int stock) {
        this.stock = stock;
    }

    public void issue(String item, int qty)
            throws OutOfStockException, InvalidQuantityException {

        if (qty <= 0) {
            throw new InvalidQuantityException();
        }

        if (qty > stock) {
            throw new OutOfStockException(qty - stock);
        }

        stock -= qty;
        System.out.println("Issued: " + qty + " " + item);
        System.out.println("Remaining stock: " + stock);
    }
}

public class StockIssue {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter starting stock: ");
        int stock = sc.nextInt();

        if (stock < 0) {
            System.out.println("Stock cannot be negative.");
            sc.close();
            return;
        }

        Warehouse w = new Warehouse(stock);

        System.out.print("Enter number of requests: ");
        int n = sc.nextInt();
        sc.nextLine();

        for (int i = 1; i <= n; i++) {
            System.out.println("\nRequest " + i);

            System.out.print("Enter item name: ");
            String item = sc.nextLine();

            System.out.print("Enter quantity: ");
            int qty = sc.nextInt();
            sc.nextLine();

            try {
                w.issue(item, qty);
            } catch (OutOfStockException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Shortage: " + e.getShortage());
            } catch (InvalidQuantityException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        sc.close();
    }
}
