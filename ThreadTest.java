import java.util.*;
public class ThreadTest {

    static volatile boolean run1 = false;
    static volatile boolean run2 = false;

    static final Random R = new Random(42);

    public static class Runner extends Thread {
        public long iterations = 0;
        public boolean run = true;
        public double computedValue = System.currentTimeMillis();
        @Override
        public void run() {
            while (run) {
                computedValue = System.currentTimeMillis();
                for (int i = 0; i < 10000; i++ ) {
                    computedValue = computedValue/2;
                }
                Thread.yield();
                iterations++;
            }
        }
    }

    public static void main(String argv[]) {
        System.err.println("threads,total_ms,perthread_ms,total_iterations");
        int n = Integer.parseInt(argv[0]);
        int k = 1;
        while ( k <= n ) {
            ThreadTest.run1 = true;
            ThreadTest.run2 = true;
            List<Runner> threads = new ArrayList<Runner>(n);
            long t = System.currentTimeMillis();
            for ( int i = 0; i < k; i++  ) {
                Runner th = new Runner();
                th.setDaemon(true);
                th.start();
                threads.add(th);
            }
            double total = (System.currentTimeMillis()-t);
            double perthread = total/k;
            ThreadTest.run1 = false;
            for (Runner thread : threads) {
                try {
                    thread.run = false;
                    thread.join(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long totalIteratons = 0;
            for (Runner thread : threads) {
                totalIteratons += thread.iterations;
            }
            System.err.println(k+","+total+","+perthread+","+totalIteratons);

            k = k*2;
        }
    }


}
