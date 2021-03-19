import java.util.ArrayList;

public class ThreadCreationTeardownTest implements Runnable {

    static volatile boolean isRunning = false;

    private final long sleep;
    
    private ThreadCreationTeardownTest(long sleep) {
        this.sleep = sleep;
    }
    
    @Override
    public void run() {
        while (isRunning) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("{");
        System.out.println("  \"tests\" : [ ");
        test(2, 1024, 100, Thread.MIN_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 100, Thread.NORM_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 100, Thread.MAX_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 20, Thread.MIN_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 20, Thread.NORM_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 20, Thread.MAX_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 5, Thread.MIN_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 5, Thread.NORM_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 5, Thread.MAX_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 3, Thread.MIN_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 3, Thread.NORM_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 3, Thread.MAX_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 2, Thread.MIN_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 2, Thread.NORM_PRIORITY);
        System.out.println("  ,");
        test(2, 1024, 2, Thread.MAX_PRIORITY);
        // These hard lock the pod when running under Kata
        // System.out.println("  ,");
        // test(2, 1024, 1, Thread.MIN_PRIORITY);
        // System.out.println("  ,");
        // test(2, 1024, 1, Thread.NORM_PRIORITY);
        // System.out.println("  ,");
        // test(2, 1024, 1, Thread.MAX_PRIORITY);
        System.out.println(" ]");
        System.out.println("}");
    }

    private static void test(int repeats, int threads, int sleep, int priority) throws InterruptedException {
        System.out.println("  {");
        System.out.println("  \"threadCreationTest2\": \"start\",");
        System.out.println("  \"repeats\"            : " + repeats + ",");
        System.out.println("  \"threads\"            : " + threads + ",");
        System.out.println("  \"sleep\"              : " + sleep + ",");
        System.out.println("  \"priority\"           : " + priority + ",");
        final long start = System.currentTimeMillis();
        long setupStart;
        long teardownStart;
        long done = start;
        long setupTotal = 0;
        long teardownTotal = 0;
        for (int k = 0; k < repeats; k++) {
            isRunning = true;
            setupStart = done;
            ArrayList<Thread> l = new ArrayList<Thread>(threads);
            for (int i = 0; i < threads; i++) {
                Thread th = new Thread(new ThreadCreationTeardownTest(sleep));
                th.start();
                th.setPriority(priority);
                l.add(th);
            }
            teardownStart = System.currentTimeMillis();
            isRunning = false;
            for (Thread thread : l) {
                thread.join(10000);
            }
            done = System.currentTimeMillis();
            setupTotal += (teardownStart - setupStart);
            teardownTotal += (done - teardownStart);
        }
        final long duration = System.currentTimeMillis() - start;
        System.out.println("  \"setupTotal\"         : " + setupTotal + ",");
        System.out.println("  \"teardownTotal\"      : " + teardownTotal + ",");
        System.out.println("  \"overallTotal\"       : " + duration);
        System.out.println("  }");
    }

}
