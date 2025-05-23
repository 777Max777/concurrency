package course.concurrency.interview.locker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RepeatableLockerServiceImpl implements LockerService {

    private final List<ReentrantLock> locks;
    private final Integer nOfReserve;

    public RepeatableLockerServiceImpl(int numberOfLocks, int numberOfReserve) {
        this.locks = new ArrayList<>();
        this.nOfReserve = numberOfReserve;

        for (int i = 0; i < numberOfLocks; i++) {
            locks.add(new ReentrantLock());
        }
    }

    @Override
    public boolean lock(Long workTime) {

        List<Integer> blockedIndexes = new ArrayList<>();

        do {
            blockedIndexes.clear();
            for (int i = 0; i < nOfReserve; i++) {
                genRand(blockedIndexes);
            }
        } while (!acquireLock(blockedIndexes));

        try {
            Thread.sleep(workTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            releaseLock(blockedIndexes);
        }

        return true;
    }

    private synchronized boolean acquireLock(List<Integer> blockedIndexes) {
        boolean isFree = blockedIndexes.stream()
                .noneMatch(i -> locks.get(i).isLocked());
        if (isFree) {
            for (Integer index : blockedIndexes) {
                if (!locks.get(index).tryLock()) {
                    System.out.println("Blocked " + index + " - " + Thread.currentThread().getName());
                    return false;
                }
                System.out.println("Locked " + index + " - " + Thread.currentThread().getName());
            }
            return true;
        }
        System.out.println("Blocked by someone(try again) - " + Thread.currentThread().getName());
        return false;
    }

    private void releaseLock(List<Integer> blockedIndexes) {
        for (Integer index : blockedIndexes) {
            locks.get(index).unlock();
        }
    }

    //порядок блокировки заранее определить нельзя
    //поток случайным образом захватывает два лока
    private int genRand(List<Integer> blockedIndexes) {
        int number;
        do {
            number = (int) (Math.random() * locks.size());
        } while (blockedIndexes.contains(number));

        blockedIndexes.add(number);

        return number;
    }
}
