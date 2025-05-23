package course.concurrency.interview.locker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleFixedLockerServiceImpl extends SimpleLockerServiceImpl implements HasCleanUp {

    private Map<Integer, Boolean> table = new ConcurrentHashMap<>();
    private ThreadLocal<List<Integer>> indexes = ThreadLocal.withInitial(ArrayList::new);

    public SimpleFixedLockerServiceImpl() {
        super();

        for (int i = 0; i < NUM_LOCKS; i++) {
            table.put(i, false);
        }
    }

    @Override
    public boolean lock(Long workTime) {
        boolean retVal = super.lock(workTime);

        indexes.get().forEach(index -> table.put(index, false));
        indexes.get().clear();

        return retVal;
    }

    @Override
    protected int genRand(int prev) {
        int number = super.genRand(prev);

        while (table.get(number)) {
            number = super.genRand(prev);
        }
        table.put(number, true);

        indexes.get().add(number);
        return number;
    }

    @Override
    public void cleanUp() {
        indexes.remove();
    }
}
