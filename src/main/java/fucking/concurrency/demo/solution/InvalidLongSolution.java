package fucking.concurrency.demo.solution;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jerry Lee(oldratlee at gmail dot com)
 */
public class InvalidLongSolution {
    //使用Atomic包装类,进行原子性读写
    AtomicLong count = new AtomicLong(0);

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        // LoadMaker.makeLoad();

        InvalidLongSolution demo = new InvalidLongSolution();

        Thread thread = new Thread(demo.getConcurrencyCheckTask());
        thread.start();

        for (int i = 0; ; i++) {
            @SuppressWarnings("UnnecessaryLocalVariable") final long l = i;
            demo.count.set(l << 32 | l);
        }
    }

    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            int c = 0;
            for (int i = 0; ; i++) {
                long l = count.get();
                long high = l >>> 32;
                long low = l & 0xFFFFFFFFL;
                if (high != low) {
                    c++;
                    System.err.printf("Fuck! Got invalid long!! check time=%s, happen time=%s(%s%%), count value=%s|%s%n",
                            i + 1, c, (float) c / (i + 1) * 100, high, low);
                } else {
                    // If remove this output, invalid long is not observed on my dev machine
                    System.out.printf("Emm... %s|%s%n", high, low);
                }
            }
        }
    }

}
