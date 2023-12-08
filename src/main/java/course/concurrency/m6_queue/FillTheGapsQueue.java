package course.concurrency.m6_queue;

public interface FillTheGapsQueue<T> {
    void enqueue(T value);
    T dequeue();
}