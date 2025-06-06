package course.concurrency.m2_async.cf;

import java.util.stream.IntStream;

public class LoadGenerator {

    public static void work() {
//        sleep();
        compute();
    }

    private static void sleep() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int compute() {
//        long start = System.currentTimeMillis();
//        int number = IntStream.range(0, 5_000_000).boxed().filter(i -> i % 2 == 0).reduce((a, b) -> b).get();
        return IntStream.range(0, 5_000_000).boxed().filter(i -> i % 2 == 0).reduce((a, b) -> b).get();
//        long end = System.currentTimeMillis();
//        System.out.println("compute time: " + (end - start));
//        return number;
    }
}