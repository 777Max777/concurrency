package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new AtomicMarkableReference<>(new Bid(0L, 0L, -2L), false);
    }

    private AtomicMarkableReference<Bid> latestBid;

    public boolean propose(Bid bid) {
        if (!latestBid.isMarked() && bid.getPrice() > latestBid.getReference().getPrice()) {
            Bid currentBid;
            do {
                currentBid = latestBid.getReference();
                if (latestBid.isMarked() || bid.getPrice() <= currentBid.getPrice()) {
                    return false;
                }
            } while (!latestBid.compareAndSet(currentBid, bid, false, false));
            notifier.sendOutdatedMessage(latestBid.getReference());
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        do {
        } while (!latestBid.attemptMark(latestBid.getReference(), true));
        return latestBid.getReference();
    }
}
