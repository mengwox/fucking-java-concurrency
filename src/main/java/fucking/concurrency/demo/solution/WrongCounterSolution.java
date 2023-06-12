package fucking.concurrency.demo.solution;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
public class WrongCounterSolution {
    private static final int INC_COUNT = 100000000;

    private volatile AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        WrongCounterSolution solution = new WrongCounterSolution();

        System.out.println("Start task thread!");
        Thread thread1 = new Thread(solution.getConcurrencyCheckTask());
        thread1.start();
        Thread thread2 = new Thread(solution.getConcurrencyCheckTask());
        thread2.start();

        thread1.join();
        thread2.join();

        int actualCounter = solution.counter.get();
        int expectedCount = INC_COUNT * 2;
        if (actualCounter != expectedCount) {
            // Even if volatile is added to the counter field,
            // On my dev machine, it's almost must occur!
            // Simple and safe solution:
            //   use AtomicInteger
            System.err.printf("Fuck! Got wrong count!! actual %s, expected: %s.%n", actualCounter, expectedCount);
        } else {
            System.out.println("Wow... Got right count!");
        }
    }

    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < INC_COUNT; ++i) {
                //这里不能用counter.get() + 1来自增,因为这也是一个int的操作,不具有原子性
                // counter.set(counter.get() + 1);
                counter.incrementAndGet();
            }
        }
    }
}
