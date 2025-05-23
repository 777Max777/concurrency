package course.concurrency.interview.locker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleLockerServiceImpl implements LockerService {

    protected static final int NUM_LOCKS = 5;
    private final List<ReentrantLock> locks;

    public SimpleLockerServiceImpl() {
        locks = new ArrayList<>();
        for (int i = 0; i < NUM_LOCKS; i++) {
            this.locks.add(new ReentrantLock());
        }
    }

    /**
     * "Захватывает два лока" подразумевает какую-то задержку в работе реальных двух ресурсов, которые должны быть недоступны другим в момент их обработки.
     * Для упрощения логики просто выставляется задержка
     */
    @Override
    public boolean lock(Long workTime) {
        int i1 = genRand(-1);
        int i2 = genRand(i1);

        if (locks.get(i1).tryLock()) {
            try {
                //В случае попытки захвата уже блокированного лока - данный поток освобождает уже захваченные им локи и поток засыпает до следующей итерации
                if (locks.get(i2).tryLock()) {
                    try {
                        System.out.println("Locked " + i1 + "," + i2 + " - " + Thread.currentThread().getName());
                        Thread.sleep(workTime);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        locks.get(i2).unlock();
                    }
                    return true;
                } else {
                    System.out.println("Blocked " + i2 + " - " + Thread.currentThread().getName());
                }
            } finally {
                locks.get(i1).unlock();
            }
        } else {
            System.out.println("Blocked " + i1 + " - " + Thread.currentThread().getName());
        }
        return false;
    }

    //порядок блокировки заранее определить нельзя
    //поток случайным образом захватывает два лока
    protected int genRand(int prev) {
        int genNumber;
        do {
            genNumber = (int) (Math.random() * NUM_LOCKS);
        } while (genNumber == prev);

        return genNumber;

    }
}
