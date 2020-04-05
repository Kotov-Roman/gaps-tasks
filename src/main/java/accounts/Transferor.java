package accounts;

import java.util.concurrent.locks.Lock;

public class Transferor implements Runnable {

    private final Account debit;
    private final Account credit;
    private final long transferringAmount;
    private int attemptCount;

    public Transferor(Account debit, Account credit, long transferringAmount) {
        this.debit = debit;
        this.credit = credit;
        this.transferringAmount = transferringAmount;
    }

    public void run() {
        while (attemptCount < 100) {
            try {
                boolean areLocksAcquired = tryToAcquireLocks();
                if (areLocksAcquired) {
                    if (isTransferringPossible()) {
                        doTransferring();
                        attemptCount = 0;
                    }
                    attemptCount++;
                    releaseLocks();
                    System.err.println(Thread.currentThread().getName() + "is going to sleep cuz can't transfer");
                    Thread.yield();
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        System.err.println(Thread.currentThread().getName() + " TERMINATED");
    }

    private void releaseLocks() {
        debit.getLock().unlock();
        credit.getLock().unlock();
    }

    private void doTransferring() {
        takeMoneyFromCredit();
        addMoneyToDebit();
        System.err.println(Thread.currentThread().getName() + " transeffing finished");
    }

    private boolean isTransferringPossible() {
        long creditAmount = credit.getAmount();
        if (creditAmount - transferringAmount < Account.MIN_AMOUNT) {
            System.err.println("credit haven't enough money");
            return false;
        }
        return true;
    }

    private boolean tryToAcquireLocks() {
        Lock debitLock = debit.getLock();
        if (debitLock.tryLock()) {
            Lock creditLock = credit.getLock();
            if (creditLock.tryLock()) {
                return true;
            }
            debitLock.unlock();
        }
        System.err.println(Thread.currentThread().getName() + " can't acquire locks");
        return false;
    }

    private void takeMoneyFromCredit() {
        credit.setAmount(credit.getAmount() - transferringAmount);
    }

    private void addMoneyToDebit() {
        debit.setAmount(debit.getAmount() + transferringAmount);
    }
}
