package accounts;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

public class Transaction implements Runnable {

    private final Account from;
    private final Account to;
    private final long amount;
    private final Random random = new Random();
    private int attempts;

    private static final Logger LOGGER = Logger.getLogger(Transaction.class.getName());


    public Transaction(Account from, Account to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public void run() {
        boolean areLocksAcquired = false;
        while (attempts < 1000) {
            try {
                attempts++;
                areLocksAcquired = tryToAcquireLocks();
                if (areLocksAcquired && isTransferringPossible()) {
                    doTransferring();
                    LOGGER.info("run() is finished for " + Thread.currentThread().getName());
                    return;

                }
                Thread.sleep(random.nextInt(50)); // prevents livelocks, helps not to exceed number of attempts
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (areLocksAcquired) {
                    releaseLocks();
                }
            }
        }
        throw new TransactionException("number of attempts exceeded");
    }

    private void releaseLocks() {
        from.getLock().unlock();
        to.getLock().unlock();
    }

    private void doTransferring() {
        LOGGER.info(String.format("transferring %s from %s to %s ", amount, from.getName(), to.getName()));
        takeMoneyFromCredit();
        addMoneyToDebit();
    }

    private boolean isTransferringPossible() {
        long creditAmount = from.getAmount();
        return creditAmount - amount >= Account.MIN_AMOUNT;
    }

    private boolean tryToAcquireLocks() {
        Lock debitLock = to.getLock();
        if (debitLock.tryLock()) {
            Lock creditLock = from.getLock();
            boolean isCreditAcquired = false;

            try {
                isCreditAcquired = creditLock.tryLock();
                if (isCreditAcquired) {
                    return true;
                }
            } finally {
                if (!isCreditAcquired) {
                    debitLock.unlock();
                }
            }
        }
        return false;
    }

    private void takeMoneyFromCredit() {
        from.setAmount(from.getAmount() - amount);
    }

    private void addMoneyToDebit() {
        to.setAmount(to.getAmount() + amount);
    }
}
