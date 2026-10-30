@FunctionalInterface
interface Notifier {
    void send(String message);
}

interface Urgent {
}

class Email implements Urgent {
    String name;

    Email(String name) {
        this.name = name;
    }
}

class SMS {
    String name;

    SMS(String name) {
        this.name = name;
    }
}

public class NotificationSender {

    public static void main(String[] args) {

        Email email = new Email("Email");
        SMS sms = new SMS("SMS");

        Notifier email_sender = message ->
                System.out.println("Email: " + message);

        Notifier sms_sender = message ->
                System.out.println("SMS: " + message);

        Notifier[] senders = {email_sender, sms_sender};

        String message = "Meeting at 5 PM";

        for (Notifier sender : senders) {
            sender.send(message);

            if (sender == email_sender && email instanceof Urgent) {
                sender.send(message);
            }
        }
    }
}