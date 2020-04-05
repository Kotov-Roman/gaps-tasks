package accounts;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private Long amount;
    private Lock lock;
    public static final long MIN_AMOUNT = 0;

    public Account(long amount) {
        this.amount = amount;
        lock = new ReentrantLock();

    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Lock getLock() {
        return lock;
    }
}
