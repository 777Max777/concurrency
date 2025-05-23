package course.concurrency.m2_async.cf.report;

import course.concurrency.m2_async.cf.LoadGenerator;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ReportServiceExecutors {

    private ExecutorService executor = Executors.newFixedThreadPool(96);

    private LoadGenerator loadGenerator = new LoadGenerator();

    public Others.Report getReport() {
        Future<Collection<Others.Item>> iFuture =
                executor.submit(() -> getItems());
        Future<Collection<Others.Customer>> customersFuture =
                executor.submit(() -> getActiveCustomers());

        try {
            Collection<Others.Customer> customers = customersFuture.get();
            Collection<Others.Item> items = iFuture.get();
            return combineResults(items, customers);
        } catch (ExecutionException | InterruptedException ex) {}

        return new Others.Report();
    }

    private Others.Report combineResults(Collection<Others.Item> items, Collection<Others.Customer> customers) {
        return new Others.Report();
    }

    private Collection<Others.Customer> getActiveCustomers() {
//        System.out.println("getActiveCustomers " + ((ThreadPoolExecutor)executor).getQueue().size());
//        System.out.println("getActiveCustomers " + ((ThreadPoolExecutor)executor).getPoolSize());
        loadGenerator.work();
        loadGenerator.work();
        return List.of(new Others.Customer(), new Others.Customer());
    }

    private Collection<Others.Item> getItems() {
//        System.out.println("getItems " + ((ThreadPoolExecutor)executor).getActiveCount());
//        System.out.println("getItems " + ((ThreadPoolExecutor)executor).getQueue().size());
        loadGenerator.work();
        return List.of(new Others.Item(), new Others.Item());
    }

    public void shutdown() {
        executor.shutdown();
    }
}
