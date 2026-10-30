
import model.BankAccount;
import service.BankService;

public class BankDataTest {

    public static void main(String[] args) throws Exception {

        BankService original = new BankService();

        original.addAccount(new BankAccount("Om", 5000));
        original.addAccount(new BankAccount("Rahul", 3000));

        original.saveData("test-accounts.dat");

        BankService restored = new BankService();

        restored.loadData("test-accounts.dat");

        if (original.getCount() != restored.getCount()) {
            throw new AssertionError("Account count does not match");
        }

        for (int i = 0; i < original.getCount(); i++) {

            BankAccount a = original.getAccount(i);
            BankAccount b = restored.getAccount(i);

            if (!a.getName().equals(b.getName())
                    || a.getBalance() != b.getBalance()) {

                throw new AssertionError("Account data does not match");
            }
        }

        System.out.println("Save and reload test passed!");
        System.out.println("Account data is unchanged.");
    }
}
