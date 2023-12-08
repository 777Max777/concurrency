package course.concurrency.m6_queue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FillTheGapsQueueTests {

    @Test
    public void queueWorksOneThread() {
        int size = 20;
        FillTheGapsQueue<Integer> queue = new FillTheGapsQueueImpl<>(size);

        for (int i = 0; i < size; i++) {
            queue.enqueue(i);
        }

        for (int expected = 0; expected < size; expected++) {
            Integer value = queue.dequeue();
            assertEquals(expected, value);
        }
    }

    @RepeatedTest(4)
    @Test
    public void addAndTake() throws InterruptedException {
        int nOfReadWrite = 400;
        ExecutorService executor = Executors.newFixedThreadPool(64);

        CountDownLatch latch = new CountDownLatch(1);
        FillTheGapsQueue<Integer> queue = new FillTheGapsQueueImpl<>(nOfReadWrite);

        for (int i = 0; i < nOfReadWrite; i++) {
            final int number = i;
            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {}
                queue.enqueue(number);
            });
            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {}
                queue.dequeue();
            });
        }
        Thread.sleep(500);
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(0, ((FillTheGapsQueueImpl<?>)queue).getSize());
    }

    @Test
    public void checkWaiting() throws InterruptedException {
        FillTheGapsQueue<Integer> queue = new FillTheGapsQueueImpl<>(1);
        Integer expectedValue = 5;

        Runnable read = () -> assertEquals(expectedValue, queue.dequeue());

        Thread reader1 = new Thread(read);
        reader1.start();
        Thread.sleep(500);

        Thread reader2 = new Thread(read);
        reader2.start();
        Thread.sleep(500);

        Thread writer1 = new Thread(() -> queue.enqueue(expectedValue));
        Thread writer2 = new Thread(() -> queue.enqueue(expectedValue));

        assertEquals(Thread.State.WAITING, reader1.getState());
        assertEquals(Thread.State.WAITING, reader2.getState());

        writer1.start();
        writer2.start();
    }
}
