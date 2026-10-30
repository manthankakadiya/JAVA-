class Counter {
    int count = 0;

    void increment() {
        count++;
    }
}

class CounterThread extends Thread {
    Counter counter;
    int times;

    CounterThread(Counter counter, int times) {
        this.counter = counter;
        this.times = times;
    }

    public void run() {
        for (int i = 0; i < times; i++) {
            counter.increment();
        }
    }
}

public class CounterRace {

    public static void main(String[] args) throws Exception {

        int threadCount = 10;
        int times = 100000;

        // Without synchronization
        Counter counter = new Counter();
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new CounterThread(counter, times);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Without synchronization: " + counter.count);
        System.out.println("Expected: " + (threadCount * times));

        // With synchronization
        Counter counter2 = new Counter();
        Thread[] threads2 = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {

            threads2[i] = new Thread(() -> {
                for (int j = 0; j < times; j++) {

                    synchronized (counter2) {
                        counter2.count++;
                    }

                }
            });

            threads2[i].start();
        }

        for (Thread thread : threads2) {
            thread.join();
        }

        System.out.println("With synchronization: " + counter2.count);
        System.out.println("Expected: " + (threadCount * times));
    }
}