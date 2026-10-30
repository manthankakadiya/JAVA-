class SeatBooking {
    int seatsLeft = 5;

    void book() {
        if (seatsLeft > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            seatsLeft--;
            System.out.println(Thread.currentThread().getName()
                    + " booked a seat.");
        } else {
            System.out.println(Thread.currentThread().getName()
                    + " failed - No seats left.");
        }
    }

    synchronized void bookSynchronized() {
        if (seatsLeft > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            seatsLeft--;
            System.out.println(Thread.currentThread().getName()
                    + " booked a seat.");
        } else {
            System.out.println(Thread.currentThread().getName()
                    + " failed - No seats left.");
        }
    }
}

public class SeatBookingRace {

    public static void main(String[] args) throws Exception {

        // Without synchronization
        SeatBooking booking = new SeatBooking();

        Thread[] threads = new Thread[10];

        System.out.println("WITHOUT SYNCHRONIZATION:");

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(
                    () -> booking.book(),
                    "Thread-" + (i + 1)
            );
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Seats left: " + booking.seatsLeft);

        // With synchronization
        SeatBooking booking2 = new SeatBooking();

        Thread[] threads2 = new Thread[10];

        System.out.println("\nWITH SYNCHRONIZATION:");

        for (int i = 0; i < 10; i++) {
            threads2[i] = new Thread(
                    () -> booking2.bookSynchronized(),
                    "Thread-" + (i + 1)
            );
            threads2[i].start();
        }

        for (Thread thread : threads2) {
            thread.join();
        }

        System.out.println("Seats left: " + booking2.seatsLeft);
    }
}