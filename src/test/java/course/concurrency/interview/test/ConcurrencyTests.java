package course.concurrency.interview.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import course.concurrency.interview.locker.HasCleanUp;
import course.concurrency.interview.locker.LockerService;
import course.concurrency.interview.job.Worker;
import course.concurrency.interview.locker.RepeatableLockerServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Всего 2 теста
 * один проверяет что настроенная конфигурация способна попытаться захватить занятный ресурс
 * другой, наоборот, что не способна занять ранее занятую блокировку
 */
public class ConcurrencyTests {

    List<Worker> workers;
    ExecutorService executor;
    LockerService lockerService;

    /**
     * Раскомментировать нужную реализацию lockerService для теста
     */
    @BeforeEach
    void prepare() {
        // Если хотим протестировать возможность попытки блокировки уже занятого ресурса
        //пытается захватить сразу 2 лока
        //должен отработать isPossibleCatchDeadlock
//        lockerService = new SimpleLockerServiceImpl();

        //если хотим протестировать исключения возможности занять уже заблокированный ресурс
        //пытается захватить сразу 2 лока
        //должен отработать isNotPossibleCatchDeadlock
//        lockerService = new SimpleFixedLockerServiceImpl();

        //Аналогично SimpleLockerServiceImpl, но можем захватить не только 2 лока и самих локов тоже может быть не 5
        //правда не уверен в реализации, что так с локами работать можно
        //должен отработать isPossibleCatchDeadlock
//        lockerService = new LockerServiceImpl(5, 2);

        //если хотим протестировать исключения возможности занять уже заблокированный ресурс
        //пытается захватить сразу 2 лока
        //должен отработать isNotPossibleCatchDeadlock
//        lockerService = new FixedLockerServiceImpl(5, 2);

        lockerService = new RepeatableLockerServiceImpl(5, 2);


        this.workers = Arrays.asList(
                new Worker(lockerService, 10000L, 3000L, 10),
                new Worker(lockerService, 5000L, 2000L, 10)
//                new Worker(lockerService, 7000L, 3000L, 10),
//                new Worker(lockerService, 6000L, 2000L, 10)
        );

        executor = Executors.newFixedThreadPool(this.workers.size());
    }

    @AfterEach
    void cleanup() {
        if (HasCleanUp.class.isAssignableFrom(lockerService.getClass())) {
            List<CompletableFuture<Void>> completableFutures = workers.stream()
                    .map(w -> CompletableFuture.runAsync(() -> ((HasCleanUp) lockerService).cleanUp(), executor))
                    .collect(Collectors.toList());

            CompletableFuture
                    .allOf(completableFutures.toArray(CompletableFuture[]::new))
                    .join();

            completableFutures.forEach(CompletableFuture::join);
        }
    }

    @Test
    void isPossibleCatchDeadlock() {
        int sum = calculateBlockedResources();

        assertTrue(sum > 0);
    }

    @Test
    void isNotPossibleCatchDeadlock() {
        int sum = calculateBlockedResources();

        assertTrue(sum == 0);
    }

    private int calculateBlockedResources() {
        List<CompletableFuture<Integer>> completableFutures = workers.stream()
                .map(w -> CompletableFuture.supplyAsync(w::start, executor))
                .collect(Collectors.toList());

        CompletableFuture
                .allOf(completableFutures.toArray(CompletableFuture[]::new))
                .join();

        return completableFutures.stream()
                .mapToInt(CompletableFuture::join)
                .sum();
    }

}
