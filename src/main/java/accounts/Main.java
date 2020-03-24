package accounts;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

  public static void main(String[] args) {

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    List<Account> accounts = Stream.generate(() -> new Account(10000))
        .limit(10)
        .collect(Collectors.toList());

    for (int i = 1; i <= accounts.size(); i++) {
      executor.execute(new Transferor(accounts));
    }
    try {
      if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // print actual value of each account ( should be more then 0)
    accounts.stream()
        .forEach(account -> System.err.println(account.getAccount().longValue()));

    Long sum = accounts.stream()
        .map(account -> account.getAccount().longValue())
        .reduce(0L, ((aLong, aLong2) -> aLong + aLong2));

    System.err.println(sum);
  }
}
