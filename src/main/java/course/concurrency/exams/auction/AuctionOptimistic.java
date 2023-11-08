package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
        latestBid = new AtomicReference<>(new Bid(0L, 0L, -2L));
    }

    private AtomicReference<Bid> latestBid;

    public boolean propose(Bid bid) {
        if (bid.getPrice() > getLatestBid().getPrice()) {
            notifier.sendOutdatedMessage(getLatestBid());
            do {
            } while (!latestBid.compareAndSet(latestBid.get(), bid));
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
