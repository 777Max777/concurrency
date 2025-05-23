package course.concurrency.interview.locker;

import course.concurrency.interview.util.StatusLock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LockerServiceImpl implements LockerService {

    private final List<ReentrantLock> locks;
    private final Integer nOfReserve;

    public LockerServiceImpl(int numberOfLocks, int numberOfReserve) {
        this.nOfReserve = numberOfReserve;
        this.locks = new ArrayList<>();

        for (int i = 0; i < numberOfLocks; i++) {
            locks.add(new ReentrantLock());
        }
    }

    @Override
    public boolean lock(Long workTime) {

        List<Integer> blockedIndexes = new ArrayList<>();
        List<StatusLock> statusLocks = IntStream.range(0, nOfReserve)
                .mapToObj(i -> {
                    int index = genRand(blockedIndexes);
                    return new StatusLock(this.locks.get(index), index);
                })
                .collect(Collectors.toList());

        if (lock(statusLocks)) {
            try {
                Thread.sleep(workTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                releaseLock(statusLocks);
            }
        } else {
            releaseLock(statusLocks);
            return false;
        }

        return true;
    }

    //порядок блокировки заранее определить нельзя
    //поток случайным образом захватывает два лока
    protected int genRand(List<Integer> blockedIndexes) {
        int number;
        do {
            number = (int) (Math.random() * locks.size());
        } while (blockedIndexes.contains(number));

        blockedIndexes.add(number);

        return number;
    }

    private boolean lock(List<StatusLock> statusLocks) {
        for (StatusLock sl : statusLocks) {
            if (sl.getLock().tryLock()) {
                sl.lock();
            } else {
                System.out.println("Blocked " + sl.getIndex() + " - " + Thread.currentThread().getName());
                return false;
            }
        }
        return true;
    }

    private void releaseLock(List<StatusLock> statusLocks) {
        for (StatusLock sl : statusLocks) {
            if (sl.getLocked()) {
                sl.getLock().unlock();
                sl.release();
            }
        }
    }

}
