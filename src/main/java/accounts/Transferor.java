package accounts;

import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

public class Transferor implements Runnable {

    private final Account debit;
    private final Account credit;
    private final long transferringAmount;
    private int attemptCount;
    private final static Logger LOGGER = Logger.getLogger(Transferor.class.getName());

    public Transferor(Account debit, Account credit, long transferringAmount) {
        this.debit = debit;
        this.credit = credit;
        this.transferringAmount = transferringAmount;
    }

    public void run() {
        boolean areLocksAcquired = false;
        while (attemptCount < 100) {
            try {
                areLocksAcquired = tryToAcquireLocks();
                if (areLocksAcquired) {
                    if (isTransferringPossible()) {
                        doTransferring();
                        attemptCount = 0;
                        continue;
                    }
                    attemptCount++;
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (areLocksAcquired) {
                    releaseLocks();
                }
            }
        }
        LOGGER.info("run() is finished for " + Thread.currentThread().getName());
    }

    private void releaseLocks() {
        credit.getLock().unlock();
        debit.getLock().unlock();
    }

    private void doTransferring() {
        LOGGER.info(String.format("transferring %s from %s to %s ", transferringAmount, credit.getName(), debit.getName()));
        takeMoneyFromCredit();
        addMoneyToDebit();
    }

    private boolean isTransferringPossible() {
        long creditAmount = credit.getAmount();
        return creditAmount - transferringAmount >= Account.MIN_AMOUNT;
    }

    private boolean tryToAcquireLocks() {
        Lock debitLock = debit.getLock();
        if (debitLock.tryLock()) {
            Lock creditLock = credit.getLock();
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
        credit.setAmount(credit.getAmount() - transferringAmount);
    }

    private void addMoneyToDebit() {
        debit.setAmount(debit.getAmount() + transferringAmount);
    }
}
