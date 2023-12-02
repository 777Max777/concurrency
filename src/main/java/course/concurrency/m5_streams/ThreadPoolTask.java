package course.concurrency.m5_streams;

import java.util.concurrent.*;

public class ThreadPoolTask {
    private int numberOfThreads = 8;

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {

        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LifoLinkedBlockingDeque<>());
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(numberOfThreads, numberOfThreads,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                (r, e) -> System.out.println("Number of task - " + e.getQueue().size())
                );
    }
}
