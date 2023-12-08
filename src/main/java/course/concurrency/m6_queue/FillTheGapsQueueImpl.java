package course.concurrency.m6_queue;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FillTheGapsQueueImpl<T> implements FillTheGapsQueue<T> {

    private final Integer size;

    private ReentrantLock lock = new ReentrantLock();
    private Condition emptyWaiter = lock.newCondition();
    private Condition filledWaiter = lock.newCondition();

    private LinkedList<T> buffer;

    public FillTheGapsQueueImpl(Integer size) {
        this.size = size;
        this.buffer = new LinkedList<>();
    }

    @Override
    public void enqueue(T value) {
        lock.lock();
        try {
            while (buffer.size() == size) {
                filledWaiter.await();
            }
            buffer.add(value);
            emptyWaiter.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public T dequeue() {
        lock.lock();
        try {
            while (buffer.isEmpty()) {
                emptyWaiter.await();
            }
            T value = buffer.removeFirst();
            filledWaiter.signal();
            return value;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public int getSize() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }
}