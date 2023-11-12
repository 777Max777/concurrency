package course.concurrency.exams.auction;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;
    private ReadWriteLock rw = new ReentrantReadWriteLock();

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new Bid(0L, 0L, -2L);
    }

    private volatile Bid latestBid;
    private volatile boolean isStopped = false;

    public boolean propose(Bid bid) {

        if (!isStopped && bid.getPrice() > latestBid.getPrice()) {
            rw.writeLock().lock();
            try {
                if (!isStopped && bid.getPrice() > latestBid.getPrice()) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            } finally {
                rw.writeLock().unlock();
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        isStopped = true;
        return latestBid;
    }
}
