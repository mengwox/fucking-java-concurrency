package fucking.concurrency.demo.solution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
public class SynchronizationOnMutableFieldSolution {
    static final int ADD_COUNT = 10000;

    static class Listener {
        // stub class
    }

    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws Exception {
        SynchronizationOnMutableFieldSolution demo = new SynchronizationOnMutableFieldSolution();

        Thread thread1 = new Thread(demo.getConcurrencyCheckTask());
        thread1.start();
        Thread thread2 = new Thread(demo.getConcurrencyCheckTask());
        thread2.start();

        thread1.join();
        thread2.join();

        int actualSize = demo.listeners.size();
        int expectedSize = ADD_COUNT * 2;
        if (actualSize != expectedSize) {
            // On my development machine, it's almost must occur!
            // Simple and safe solution:
            //   final List field and use concurrency-safe List, such as CopyOnWriteArrayList
            System.err.printf("Fuck! Lost update on mutable field! actual %s expected %s.%n", actualSize, expectedSize);
        } else {
            System.out.println("Emm... Got right answer!!");
        }
    }

    public void addListener(Listener listener) {
        if (listener == null) {
            return;
        }
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        public void run() {
            System.out.println("ConcurrencyCheckTask started!");
            for (int i = 0; i < ADD_COUNT; ++i) {
                addListener(new Listener());
            }
            System.out.println("ConcurrencyCheckTask stopped!");
        }
    }
}
