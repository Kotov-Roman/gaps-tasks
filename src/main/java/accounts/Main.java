package accounts;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        Account account1 = new Account(100);
        Account account2 = new Account(100);
        Account account3 = new Account(100);
        Account account4 = new Account(100);
        Account account5 = new Account(100);
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

        while (!executor.isTerminated()) {
        }
        System.out.println("account5 should have: " + sum);
        System.out.println("current amount for account5: p" + account5.getAmount());
        System.out.println("END");
    }
}
