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
        Bid currentBid;
        do {
            currentBid = latestBid.get();
            if (bid.getPrice() <= currentBid.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(currentBid, bid));
        notifier.sendOutdatedMessage(getLatestBid());
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
