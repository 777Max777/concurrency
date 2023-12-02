package course.concurrency.m5_streams;

import java.util.concurrent.LinkedBlockingDeque;

public class LifoLinkedBlockingDeque<E> extends LinkedBlockingDeque<E> {

    @Override
    public E take() throws InterruptedException {
        return super.takeLast();
    }
}
