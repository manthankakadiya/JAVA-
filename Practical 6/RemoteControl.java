interface Switchable {

    void on();

    void off();

    default void toggle() {
        on();
        off();
    }
}

class Fan implements Switchable {

    public void on() {
        System.out.println("Fan is ON");
    }

    public void off() {
        System.out.println("Fan is OFF");
    }
}

class Light implements Switchable {

    public void on() {
        System.out.println("Light is ON");
    }

    public void off() {
        System.out.println("Light is OFF");
    }
}

@FunctionalInterface
interface Permission {
    boolean maySwitchOn(String device, int hour);
}

public class RemoteControl {

    public static void main(String[] args) {

        Fan fan = new Fan();
        Light light = new Light();

        Switchable[] devices = {fan, light};

        System.out.println("Toggling devices:");

        for (Switchable device : devices) {
            device.toggle();
        }

        Permission p1 = new Permission() {
            public boolean maySwitchOn(String device, int hour) {
                return hour >= 6 && hour <= 22;
            }
        };

        System.out.println("Fan at 10: " + p1.maySwitchOn("Fan", 10));
        System.out.println("Light at 2: " + p1.maySwitchOn("Light", 2));

        Permission p2 = (device, hour) -> hour >= 6 && hour <= 22;

        System.out.println("\nUsing lambda:");
        System.out.println("Fan at 8: " + p2.maySwitchOn("Fan", 8));
        System.out.println("Light at 3: " + p2.maySwitchOn("Light", 3));
    }
}