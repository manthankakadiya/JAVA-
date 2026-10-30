
public class AccountServiceTest {

    public static void main(String[] args) {

        addingAccountIncreasesCount();
        addingTwoAccountsIncreasesCountByTwo();
        invalidAccountThrowsException();

        System.out.println("\nAll tests passed!");
    }

    static void addingAccountIncreasesCount() {
        AccountService service = new AccountService();

        service.addAccount(new Account("Om", 1000));

        if (service.getCount() != 1) {
            throw new AssertionError("Count should be 1");
        }

        System.out.println("Test 1 passed");
    }

    static void addingTwoAccountsIncreasesCountByTwo() {
        AccountService service = new AccountService();

        service.addAccount(new Account("Om", 1000));
        service.addAccount(new Account("Rahul", 2000));

        if (service.getCount() != 2) {
            throw new AssertionError("Count should be 2");
        }

        System.out.println("Test 2 passed");
    }

    static void invalidAccountThrowsException() {
        AccountService service = new AccountService();

        try {
            service.addAccount(null);

            throw new AssertionError("Exception was not thrown");

        } catch (IllegalArgumentException expected) {
            System.out.println("Test 3 passed");
        }
    }
}
