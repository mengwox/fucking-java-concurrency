package fucking.concurrency.demo.solution;

/**
 * todo 注意i=Integer.MAX_VALUE后的报错
 *
 * @see fucking.concurrency.demo.InvalidLongDemo
 */
public class InvalidLongSolution {
    volatile long count = 0L;

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        // LoadMaker.makeLoad();

        InvalidLongSolution demo = new InvalidLongSolution();

        Thread thread = new Thread(demo.getConcurrencyCheckTask());
        thread.start();

        for (int i = 0; ; i++) {
            @SuppressWarnings("UnnecessaryLocalVariable") final long l = i;
            demo.count = l << 32 | l;
        }
    }

    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        public void run() {
            int c = 0;
            for (int i = 0; ; i++) {
                long l = count;
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
