import java.util.Scanner;

public class Driver1 {

    public static void main(String[] args) {

        String[] logs = {
                "10:05 Manthan Hello there",
                "10:10 Om How are you?",
                "10:15",
                "10:20 Bhavy Good morning"
        };

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter keyword: ");
        String keyword = sc.nextLine();

        String output = ChatFilter.filterChats(logs, keyword);

        System.out.println(output);

        sc.close();
    }
}