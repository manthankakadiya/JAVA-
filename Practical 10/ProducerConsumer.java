class SharedBuffer {

    private int item;
    private boolean available = false;

    public synchronized void produce(int value) throws InterruptedException {

        // Wait if buffer is full
        while (available) {
            wait();
        }

        item = value;
        available = true;

        System.out.println("Produced: " + value);

        // Notify consumer
        notify();
    }

    // Consumer removes an item
    public synchronized int consume() throws InterruptedException {

        // Wait if buffer is empty
        while (!available) {
            wait();
        }

        int value = item;
        available = false;

        System.out.println("Consumed: " + value);

        // Notify producer
        notify();

        return value;
    }
}

public class ProducerConsumer {

    public static void main(String[] args) {

        SharedBuffer buffer = new SharedBuffer();

        int numberOfItems = 10;

        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= numberOfItems; i++) {
                    buffer.produce(i);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= numberOfItems; i++) {
                    buffer.consume();
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("All items produced and consumed.");
    }
}