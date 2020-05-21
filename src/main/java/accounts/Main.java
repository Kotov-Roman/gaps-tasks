package accounts;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Account account1 = new Account(1000, "First account");
        Account account2 = new Account(1000, "Second account");
        Account account3 = new Account(1000, "Third account");

        List<Transaction> transactions1 = generateTransactions(account1, account2, 10, 100);
        List<Transaction> transactions2 = generateTransactions(account2, account3, 10, 100);
        List<Transaction> transactions3 = generateTransactions(account3, account1, 10, 100);
        List<Transaction> transactions4 = generateTransactions(account2, account1, 10, 50);

        transactions1.addAll(transactions2);
        transactions1.addAll(transactions3);
        transactions1.addAll(transactions4);

        transactions1.forEach(executor::execute);
        executor.shutdown();

        try {
            executor.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.info(account1.toString());
        LOGGER.info(account2.toString());
        LOGGER.info(account3.toString());

        assert account1.getAmount() == 1500;
        assert account2.getAmount() == 500;
        assert account3.getAmount() == 1000;
    }

    private static List<Transaction> generateTransactions(Account from, Account to, int amount, int limit) {
        return Stream.generate(() -> new Transaction(from, to, amount))
                .limit(limit)
                .collect(Collectors.toList());

    }
}
