import java.util.*;

public class UniqueVisitors {
    public static void main(String[] args) {

        List<String> logins = Arrays.asList("Manthan", "Bhavy", "Om", "Manthan","Om", "Bhavy", "Neha", "Amit" );

        // HashSet for unique count
        HashSet<String> uniqueUsers = new HashSet<>(logins);

        // LinkedHashSet for first-seen order
        LinkedHashSet<String> orderedUsers = new LinkedHashSet<>(logins);

        System.out.println("Unique users: " + uniqueUsers.size());

        System.out.println("First three distinct users:");

        int count = 0;

        for (String user : orderedUsers) {
            System.out.println(user);
            count++;

            if (count == 3)
                break;
        }
    }
}