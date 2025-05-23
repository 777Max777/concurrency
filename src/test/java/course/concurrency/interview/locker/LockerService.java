package course.concurrency.interview.locker;

public interface LockerService {

    boolean lock(Long workTime);
}
