package course.concurrency.interview.job;

import course.concurrency.interview.locker.LockerService;

public class Worker {

    private final Long waitTime;
    private final Long workTime;
    private final Integer iterations;
    private final LockerService lockerService;

    private final Long iterationTime;

    public Worker(LockerService lockerService, Long waitTime, Long workTime, Integer iterations) {
        this.lockerService = lockerService;
        this.waitTime = waitTime;
        this.workTime = workTime;
        this.iterations = iterations;
        this.iterationTime = waitTime + workTime;
    }

    public Integer start() {
        int counterBlockedLockers = 0;
        try {
            Thread.sleep(waitTime);
            for (int i = 0; i < iterations; i++) {

                long currentTime = System.currentTimeMillis();
                if (lockerService.lock(workTime)) {
                    //Если суммарное время итерации превзойдёт время лока и время ожидания начала следующей итерации, то следующая итерация должна начинаться сразу.
                    if (iterationTime < System.currentTimeMillis() - currentTime) {
                        System.out.println("Time is over (continue)");
                        continue;
                    }

                } else {
                    counterBlockedLockers++;
                }
                Thread.sleep(waitTime);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return counterBlockedLockers;
    }
}
