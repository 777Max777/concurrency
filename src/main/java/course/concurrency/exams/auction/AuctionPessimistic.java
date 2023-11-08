package course.concurrency.exams.auction;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;
    private ReadWriteLock rw = new ReentrantReadWriteLock();
    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new Bid(0L, 0L, -2L);
    }

    private Bid latestBid;

    public boolean propose(Bid bid) {
        if (bid.getPrice() > getLatestBid().getPrice()) {
            notifier.sendOutdatedMessage(getLatestBid());
            rw.writeLock().lock();
            try {
                latestBid = bid;
            } finally {
                rw.writeLock().unlock();
            }
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        rw.readLock().lock();
        try {
            return latestBid;
        } finally {
            rw.readLock().unlock();
        }
    }
}
