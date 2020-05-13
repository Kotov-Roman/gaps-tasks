package accounts;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main {
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        Account account1 = new Account(100, "First account");
        Account account2 = new Account(100, "Second account");
        Account account3 = new Account(100, "Third account");
        Account account4 = new Account(100, "Fourth account");
        Account account5 = new Account(100, "Fifth account");
        long sum = account1.getAmount() + account2.getAmount() + account3.getAmount() + account4.getAmount()
                + account5.getAmount();

        Transferor transferor1 = new Transferor(account2, account1, 10);
        Transferor transferor2 = new Transferor(account3, account2, 10);
        Transferor transferor3 = new Transferor(account4, account3, 10);
        Transferor transferor4 = new Transferor(account1, account4, 10);
        Transferor transferor5 = new Transferor(account5, account4, 10);

        executor.execute(transferor1);
        executor.execute(transferor2);
        executor.execute(transferor3);
        executor.execute(transferor4);
        executor.execute(transferor5);

        executor.shutdown();

        try {
            executor.awaitTermination(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.info("account5 should have: " + sum);
        LOGGER.info("current amount for account5: " + account5.getAmount());
        LOGGER.info("END");
    }
}
