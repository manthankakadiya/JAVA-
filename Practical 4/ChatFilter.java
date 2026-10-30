public class ChatFilter {

    public static String filterChats(String[] logs, String keyword) {

        StringBuilder result = new StringBuilder();
        int count = 0;

        for (String line : logs) {

            String[] parts = line.split(" ", 3);

            if (parts.length < 3) {
                continue;
            }

            String time = parts[0];
            String user = parts[1];
            String message = parts[2];

            if (message.toLowerCase().contains(keyword.toLowerCase())) {
                count++;
                result.append(time)
                      .append(" ")
                      .append(user)
                      .append(": ")
                      .append(message)
                      .append("\n");
            }
        }

        return "Matches: " + count + "\n" + result.toString();
    }
}