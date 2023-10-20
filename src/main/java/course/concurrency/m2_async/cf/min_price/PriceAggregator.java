package course.concurrency.m2_async.cf.min_price;

import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        if (!CollectionUtils.isEmpty(shopIds)) {
            CompletableFuture<Double>[] cfArray = shopIds.stream()
                    .map(shopId -> CompletableFuture.supplyAsync(
                                    () -> priceRetriever.getPrice(itemId, shopId), Executors.newCachedThreadPool()
                            )
                            .completeOnTimeout(Double.NaN, 2600, TimeUnit.MILLISECONDS)
                            .exceptionally(ex -> Double.NaN))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(cfArray).join();

            List<Double> prices = Arrays.stream(cfArray)
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            if (!allEqualEachOther(prices))
                return prices.stream().min(Double::compareTo).get();
        }
        return Double.NaN;
//        return 0;
    }

    private boolean allEqualEachOther(List<Double> prices) {
        return prices.stream()
                .allMatch(prices.get(0)::equals);
    }
}
