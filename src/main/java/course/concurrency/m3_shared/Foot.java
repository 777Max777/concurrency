package course.concurrency.m3_shared;

public class Foot implements Runnable {

    private final String stepName;

    private String lock = "lock";
    private static volatile int currentLeg = 0;

    public Foot(String stepName) {
        this.stepName = stepName;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock) {
                if (currentLeg == 0 && stepName.equals("left")
                        || currentLeg == 1 && stepName.equals("right")) {
                    try {
                        System.out.println(stepName);
                        Thread.sleep(2000);
                        currentLeg = (currentLeg + 1) % 2;
                        lock.notify();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        }
    }

    public static void main(String[] args) {
        Foot left = new Foot("left");
        Foot right = new Foot("right");

        Thread thread = new Thread(left);
        Thread thread2 = new Thread(right);

        thread.start();
        thread2.start();
    }
}
