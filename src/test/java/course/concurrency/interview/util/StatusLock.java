package course.concurrency.interview.util;

import java.util.concurrent.locks.ReentrantLock;

public class StatusLock {

    private ReentrantLock lock;
    private Integer index;
    private boolean isLocked;

    public StatusLock(ReentrantLock lock, Integer index) {
        this.lock = lock;
        this.index = index;
    }

    public void lock() {
        this.isLocked = true;
        System.out.println("Locked " + this.index + " - " + Thread.currentThread().getName());
    }

    public void release() {
        this.isLocked = false;
        System.out.println("Released " + this.index + " - " + Thread.currentThread().getName());
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public boolean getLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
